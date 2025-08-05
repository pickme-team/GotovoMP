package org.yaabelozerov.gotovomp.presentation.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.koin.compose.viewmodel.koinViewModel
import org.yaabelozerov.gotovomp.Const
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO
import org.yaabelozerov.gotovomp.domain.Pager
import org.yaabelozerov.gotovomp.domain.PagerState
import org.yaabelozerov.gotovomp.presentation.components.RecipeCard
import org.yaabelozerov.gotovomp.presentation.util.Nav
import org.yaabelozerov.gotovomp.viewModels.FeedScreenVM

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FeedScreen(
    navCtrl: NavHostController,
    feedScreenVM: FeedScreenVM = koinViewModel(),
    animatedScope: AnimatedContentScope,
) {
    val pager = feedScreenVM.pager
    val pagerState by pager.state.collectAsState()
    val lazyListState = rememberLazyListState()
    LaunchedEffect(Unit) {
        pager.refresh()
    }
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = pagerState is PagerState.Loading,
        onRefresh = pager::refresh,
    ) {
        LazyColumn(
            state = lazyListState
        ) {
            when (val s = pagerState) {
                is PagerState.HasContent -> {
                    recipeList(content = s, onLoadMore = pager::loadNextPage, onNavigateTo = {
                        navCtrl.navigate(Nav.VIEW.route + "/${it}")
                    }, addModifierWithId = {
                        Modifier.sharedBounds(
                            rememberSharedContentState("view${it}"),
                            animatedVisibilityScope = animatedScope
                        )
                    })
                }

                is PagerState.Error -> item {
                    Text(s.error.toString())
                }

                else -> item {
                    CircularProgressIndicator()
                }
            }

        }
    }
}


private fun LazyListScope.recipeList(
    content: PagerState.HasContent<RecipeDTO>,
    onLoadMore: () -> Unit,
    onNavigateTo: (Long) -> Unit,
    addModifierWithId: @Composable (Long) -> Modifier,
) {
    items(content.content) { recipe ->
        RecipeCard(
            recipe = recipe,
            imageUrl = Const.placeholderImages.random(),
            onClick = { onNavigateTo(recipe.id) },
            modifier = Modifier.fillParentMaxWidth().animateItem()
                .then(addModifierWithId(recipe.id))
        )
    }
    if (content.hasMore) {
        item {
            LaunchedEffect(Unit) { onLoadMore() }
        }
    }
}