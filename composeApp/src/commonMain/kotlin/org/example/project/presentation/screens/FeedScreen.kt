package org.example.project.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import org.example.project.domain.DomainError
import org.example.project.presentation.util.Nav
import org.example.project.presentation.util.bouncyClickable
import org.example.project.viewModels.FeedScreenVM

private val images = listOf(
    "https://lobsterfrommaine.com/wp-content/uploads/fly-images/1577/20210517-Pasta-alla-Gricia-with-Lobster3010-1024x576-c.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/8/86/Gnocchi_di_ricotta_burro_e_salvia.jpg",
    "https://bakesbybrownsugar.com/wp-content/uploads/2019/11/Pecan-Cinnamon-Rolls-84-500x500.jpg",
    "https://bestlah.sg/wp-content/uploads/2024/07/Best-Peranakan-Food-Singapore.jpeg"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    navCtrl: NavHostController,
    feedScreenVM: FeedScreenVM = viewModel(),
) {
    val uiState by feedScreenVM.state.collectAsState()
    PullToRefreshBox(
        modifier = Modifier.fillMaxSize(),
        isRefreshing = uiState.isLoading,
        onRefresh = feedScreenVM::loadRecipes,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Text(
                    "Рекомендации",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item(span = { GridItemSpan(2) }) {
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
            items(uiState.recipes, key = { it.id }) { recipe ->
                RecipeCard(recipe.name, images.random(), onClick = { navCtrl.navigate(Nav.VIEW.route + "/${recipe.id}") })
            }
        }
    }
}

@Composable
fun RecipeCard(title: String, imageUrl: String, onClick: () -> Unit) =
    Card(modifier = Modifier.fillMaxWidth().aspectRatio(0.8f).bouncyClickable(onClick = onClick)) {
        var imageLoaded by rememberSaveable { mutableStateOf(false) }
        Box(
            modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomStart
        ) {
            val fadeColor =
                MaterialTheme.colorScheme.run { if (!isSystemInDarkTheme()) onBackground else background }
            val fadeInverse =
                MaterialTheme.colorScheme.run { if (isSystemInDarkTheme()) onBackground else background }
            AsyncImage(modifier = Modifier.fillMaxSize().drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        0f to Color.Transparent, 0.85f to fadeColor.copy(0.75f)
                    )
                )
            },
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                onSuccess = { imageLoaded = true })
            Text(
                text = title,
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 3,
                style = MaterialTheme.typography.headlineLarge.copy(
                    lineBreak = LineBreak.Paragraph,
                    color = fadeInverse,
                    fontWeight = FontWeight.Black
                )
            )
        }
    }
