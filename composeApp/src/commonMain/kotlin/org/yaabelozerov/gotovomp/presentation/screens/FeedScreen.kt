package org.yaabelozerov.gotovomp.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.koin.compose.viewmodel.koinViewModel
import org.yaabelozerov.gotovomp.Const
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO
import org.yaabelozerov.gotovomp.domain.PagerState
import org.yaabelozerov.gotovomp.presentation.components.RecipeCard
import org.yaabelozerov.gotovomp.presentation.components.ScreenHeader
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
    LaunchedEffect(Unit) {
        pager.refresh()
    }
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = pagerState is PagerState.Loading,
        onRefresh = pager::refresh,
    ) {
        LazyColumn {
            item {
                ScreenHeader("Рекомендации")
            }
            when (val s = pagerState) {
                is PagerState.HasContent -> {
                    recipeList(
                        content = s,
                        onLoadMore = { if (it) pager.refresh() else pager.loadNextPage() },
                        onNavigateTo = {
                            navCtrl.navigate(Nav.VIEW.route + "/${it}")
                        },
                        addModifierWithId = {
                            Modifier.sharedBounds(
                                rememberSharedContentState("view${it}"),
                                animatedVisibilityScope = animatedScope
                            )
                        })
                }

                is PagerState.Error -> item {
                    Text(s.error.toString())
                }

                else -> announcement(content = {
                    Text("Загружаем рецепты для вас")
                }, onLoadMore = {})
            }

        }
    }
}

private fun LazyListScope.announcement(content: @Composable () -> Unit, onLoadMore: () -> Unit) {
    item {
        LaunchedEffect(Unit) { onLoadMore() }
        Row(
            modifier = Modifier.fillParentMaxWidth().animateItem()
                .background(MaterialTheme.colorScheme.primaryContainer),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "<3",
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.headlineMedium
            )
            content()
        }
    }
}


private fun LazyListScope.recipeList(
    content: PagerState.HasContent<RecipeDTO>,
    onLoadMore: (Boolean) -> Unit,
    onNavigateTo: (Long) -> Unit,
    addModifierWithId: @Composable (Long) -> Modifier,
) {
    items(content.content, key = { it.id }) { recipe ->
        val imageUrl = rememberSaveable {
            Const.placeholderImages.random()
        }
        RecipeCard(
            recipe = recipe,
            imageUrl = imageUrl,
            onClick = { onNavigateTo(recipe.id) },
            modifier = Modifier.fillParentMaxWidth().animateItem()
                .then(addModifierWithId(recipe.id))
        )
    }
    announcement(content = {
        AnimatedContent(
            content.hasMore, transitionSpec = {
                slideInVertically() + fadeIn() + scaleIn() togetherWith slideOutVertically(
                    targetOffsetY = { it / 2 }) + fadeOut() + scaleOut()
            }) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (it) "Загружаем рецепты для вас" else "На этом всё")
                if (!it) {
                    OutlinedCard(modifier = Modifier.clip(MaterialTheme.shapes.medium).clickable {
                        onLoadMore(true)
                    }, shape = MaterialTheme.shapes.medium) {
                        Text(
                            "Обновить",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }) {
        if (content.hasMore) onLoadMore(false)
    }
}