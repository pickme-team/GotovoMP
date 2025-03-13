package org.example.project.presentation.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import org.example.project.Const
import org.example.project.data.network.model.RecipeDTO
import org.example.project.domain.DomainError
import org.example.project.presentation.components.AlternativeRecipeCard
import org.example.project.presentation.util.Nav
import org.example.project.presentation.util.bouncyClickable
import org.example.project.viewModels.FeedScreenVM

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.FeedScreen(
    navCtrl: NavHostController,
    feedScreenVM: FeedScreenVM = viewModel(),
    animatedScope: AnimatedContentScope,
) {
    val uiState by feedScreenVM.state.collectAsState()
    val recipes = feedScreenVM.pager.collectAsLazyPagingItems().also { println(it.itemCount) }
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
        LazyColumn(
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    "Рекомендации",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                uiState.error?.let {
                    Text(
                        when (it) {
                            is DomainError.NetworkClientError -> "Client Error"
                            is DomainError.NetworkServerError -> "Server Error"
                            DomainError.Unknown -> "Unknown Error"
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            items(recipes.itemCount) { index ->
                val recipe = recipes[index] ?: return@items
                AlternativeRecipeCard(
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeCard(recipe: RecipeDTO, imageUrl: String, onClick: () -> Unit, modifier: Modifier = Modifier) =
    Card(modifier = modifier.fillMaxWidth().aspectRatio(0.8f).bouncyClickable(onClick = onClick, shrinkSize = 0.975f)) {
        var imageLoaded by rememberSaveable { mutableStateOf(false) }
        val fadeColor =
            MaterialTheme.colorScheme.run { if (!isSystemInDarkTheme()) onBackground else background }
        val fadeInverse =
            MaterialTheme.colorScheme.run { if (isSystemInDarkTheme()) onBackground else background }
        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomStart
        ) {
            var lineCount by remember { mutableStateOf(1) }
            AsyncImage(modifier = Modifier.fillMaxSize().drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.65f - .15f*lineCount to Color.Transparent,
                        1f - .15f*lineCount  to fadeColor.copy(0.85f)
                    )
                )
            },
                model = imageUrl,
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                onSuccess = { imageLoaded = true })

            Column(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = recipe.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 3,
                    style = MaterialTheme.typography.displayMedium.copy(
                        lineBreak = LineBreak.Paragraph,
                        color = fadeInverse,
                        fontWeight = FontWeight.Black
                    ),
                    onTextLayout = {
                        lineCount = it.lineCount
                    },
                )
                recipe.ingredients.run {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Card(shape = MaterialTheme.shapes.small, colors = CardDefaults.outlinedCardColors(containerColor = fadeInverse, contentColor = fadeColor)) {
                            Text(text = recipe.author.run { firstName?.plus(" ")?.plus(lastName) ?: username }, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                        }
                        forEach {
                            Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.outlinedCardColors(containerColor = fadeInverse, contentColor = fadeColor)) {
                                Text(it.name, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontStyle = FontStyle.Italic)
                            }
                        }
                        Spacer(modifier = Modifier.padding(bottom = 16.dp))
                    }
                }
            }
        }
    }
