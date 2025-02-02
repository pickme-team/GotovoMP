package org.example.project.presentation.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import org.example.project.data.network.model.RecipeDTO
import org.example.project.presentation.util.Nav
import org.example.project.presentation.util.bouncyClickable
import org.example.project.viewModels.PersonalVM

private val images = listOf(
    "https://lobsterfrommaine.com/wp-content/uploads/fly-images/1577/20210517-Pasta-alla-Gricia-with-Lobster3010-1024x576-c.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg",
    "https://upload.wikimedia.org/wikipedia/commons/8/86/Gnocchi_di_ricotta_burro_e_salvia.jpg",
    "https://bakesbybrownsugar.com/wp-content/uploads/2019/11/Pecan-Cinnamon-Rolls-84-500x500.jpg",
    "https://bestlah.sg/wp-content/uploads/2024/07/Best-Peranakan-Food-Singapore.jpeg"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalScreen(navCtrl: NavHostController, modifier: Modifier = Modifier, viewModel: PersonalVM) {
    val uiState by viewModel.state.collectAsState()
    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = uiState.isLoading,
        onRefresh = viewModel::fetchRecipes,
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp)) {
            item {
                Section(title = "Созданные мной", items = uiState.recipes.recipes, icon = Icons.Default.Favorite, onClick = {
                   navCtrl.navigate("${Nav.VIEW.route}/$it")
                }, actionButton = {
                    SmallFloatingActionButton(onClick = { navCtrl.navigate(Nav.CREATE.route) }, elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 2.dp,
                        hoveredElevation = 2.dp
                    )) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                })
            }
        }
    }
}

@Composable
private fun Section(title: String, items: List<RecipeDTO>, onClick: (Long) -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null, actionButton: (@Composable () -> Unit)? = null) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            icon?.let { imageVector ->
                Icon(imageVector, contentDescription = title)
            }
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.weight(1f))
            actionButton?.invoke()
        }
        val rowScrollState = rememberLazyListState()
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp), state = rowScrollState, contentPadding = PaddingValues(horizontal = 24.dp)) {
            items(items, key = { it.id }) {
                Recipe(title = it.name, imageUrl = images.random(), onClick = { onClick(it.id) })
            }
        }
    }
}

@Composable
private fun Recipe(
    title: String, imageUrl: String, modifier: Modifier = Modifier, onClick: () -> Unit
) {
    var isPopupOpen by rememberSaveable { mutableStateOf(false) }
    val cardWidth = 192.dp
    Box(
        modifier = modifier.width(cardWidth).aspectRatio(0.8f)
    ) {
        RecipeCard(title, imageUrl, onClick = onClick, onHold = { isPopupOpen = true })
        RecipeDropdown(isPopupOpen = isPopupOpen, onDismissRequest = { isPopupOpen = false }, modifier = Modifier.width(cardWidth))
    }
}

@Composable
fun RecipeCard(title: String, imageUrl: String, onClick: () -> Unit, onHold: () -> Unit) = Card(
    modifier = Modifier.fillMaxSize().bouncyClickable(onClick = {
        println("Clicked $title")
        onClick()
    }, onHold = {
        println("Hold $title")
        onHold()
    })
) {
    var imageLoaded by rememberSaveable { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomStart
    ) {
        val fadeColor = MaterialTheme.colorScheme.run { if (!isSystemInDarkTheme()) onBackground else background }
        val fadeInverse = MaterialTheme.colorScheme.run { if (isSystemInDarkTheme()) onBackground else background }
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
            style = MaterialTheme.typography.headlineSmall.copy(lineBreak = LineBreak.Paragraph, color = fadeInverse, fontWeight = FontWeight.Black)
        )
    }
}

@Composable
private fun RecipeDropdown(isPopupOpen: Boolean, onDismissRequest: () -> Unit, modifier: Modifier = Modifier) = DropdownMenu(
    modifier = modifier,
    onDismissRequest = onDismissRequest,
    expanded = isPopupOpen,
    shape = MaterialTheme.shapes.medium,
    offset = DpOffset(0.dp, 16.dp)
) {
    RecipeDropdownItem(text = "Поделиться", icon = Icons.Default.Share) { onDismissRequest() }
    RecipeDropdownItem(text = "Удалить", icon = Icons.Default.Delete) { onDismissRequest() }
}

@Composable
private fun RecipeDropdownItem(text: String, icon: ImageVector? = null, onClick: () -> Unit) =
    DropdownMenuItem(text = {
        Text(
            text,
            modifier = Modifier.padding(start = 4.dp)
        )
    }, leadingIcon = {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }, onClick = onClick
    )