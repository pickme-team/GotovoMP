package org.yaabelozerov.gotovomp.presentation.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.PagingData
import app.cash.paging.compose.collectAsLazyPagingItems
import coil3.compose.LocalPlatformContext
import org.koin.compose.viewmodel.koinViewModel
import org.yaabelozerov.gotovomp.Const
import org.yaabelozerov.gotovomp.domain.DomainError
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
    val recipes = feedScreenVM.recipes.collectAsLazyPagingItems()
    LaunchedEffect(Unit) {
        recipes.refresh()
    }
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = when (recipes.loadState.refresh) {
            is LoadState.Error, is LoadState.NotLoading -> false
            else -> true
        },
        onRefresh = {
            recipes.refresh()
        },
    ) {
        LazyColumn {
            item {
                ScreenHeader("Рекоммендации")
            }
            items(recipes.itemCount) { index ->
                val recipe = recipes[index] ?: return@items
                RecipeCard(
                    recipe = recipe,
                    imageUrl = Const.placeholderImages.random(),
                    onClick = { navCtrl.navigate(Nav.VIEW.route + "/${recipe.id}") },
                    modifier = Modifier.fillParentMaxWidth().animateItem().sharedBounds(
                        rememberSharedContentState("view${recipe.id}",),
                        animatedVisibilityScope = animatedScope
                    )
                )
            }
        }
    }
}
