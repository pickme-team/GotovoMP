package org.yaabelozerov.gotovomp.presentation.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.material3.HorizontalDivider
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
import io.github.aakira.napier.Napier
import org.yaabelozerov.gotovomp.util.Const
import org.yaabelozerov.gotovomp.data.network.model.RecipeDTO
import org.yaabelozerov.gotovomp.presentation.components.ScreenHeader
import org.yaabelozerov.gotovomp.presentation.util.Nav
import org.yaabelozerov.gotovomp.presentation.util.bouncyClickable
import org.yaabelozerov.gotovomp.viewModels.PersonalVM

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PersonalScreen(navCtrl: NavHostController, modifier: Modifier = Modifier, animatedScope: AnimatedContentScope, viewModel: PersonalVM) {
    val uiState by viewModel.state.collectAsState()
    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = uiState.isLoading,
        onRefresh = viewModel::fetchRecipes,
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 0.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                ScreenHeader("Коллекции")
                HorizontalDivider()

            }
            item {
                Section(title = "Созданные мной", items = uiState.recipes, animatedScope = animatedScope, icon = Icons.Filled.Add, onClick = {
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
                }, delete = { viewModel.deleteRecipe(it) } )
            }
            item {
                Section(
                    title = "Избранные",
                    items = emptyList(),
                    animatedScope = animatedScope,
                    icon = Icons.Default.Favorite,
                    onClick = {
                        navCtrl.navigate("${Nav.VIEW.route}/$it")
                    },
                    actionButton = {
                        SmallFloatingActionButton(onClick = { navCtrl.navigate(Nav.CREATE.route) }, elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 0.dp,
                            focusedElevation = 2.dp,
                            hoveredElevation = 2.dp
                        )) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    },
                    delete = { viewModel.deleteRecipe(it) }
                )
            }

        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.Section(title: String, items: List<RecipeDTO>, onClick: (Long) -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null, animatedScope: AnimatedContentScope, actionButton: (@Composable () -> Unit)? = null, delete: (Long) -> Unit) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                Recipe(title = it.name, imageUrl = Const.placeholderImages.random(), onClick = { onClick(it.id) }, delete = { delete(it.id) }, modifier = Modifier.sharedBounds(rememberSharedContentState("view${it.id}"), animatedVisibilityScope = animatedScope))
            }
        }
    }
}

@Composable
private fun Recipe(
    title: String, imageUrl: String, modifier: Modifier = Modifier, onClick: () -> Unit, delete: () -> Unit
) {
    var isPopupOpen by rememberSaveable { mutableStateOf(false) }
    val cardWidth = 192.dp
    Box(
        modifier = modifier.width(cardWidth).aspectRatio(0.8f)
    ) {
        RecipeCard(title, imageUrl, onClick = onClick, onHold = { isPopupOpen = true })
        RecipeDropdown(isPopupOpen = isPopupOpen, onDismissRequest = { isPopupOpen = false }, modifier = Modifier.width(cardWidth), delete = { delete() })
    }
}

@Composable
fun RecipeCard(title: String, imageUrl: String, onClick: () -> Unit, onHold: () -> Unit) = Card(
    modifier = Modifier.fillMaxSize().bouncyClickable(onClick = {
        Napier.d { "Clicked $title" }
        onClick()
    }, onHold = {
        Napier.d { "Hold $title" }
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
private fun RecipeDropdown(isPopupOpen: Boolean, onDismissRequest: () -> Unit, modifier: Modifier = Modifier, delete: () -> Unit) = DropdownMenu(
    modifier = modifier,
    onDismissRequest = onDismissRequest,
    expanded = isPopupOpen,
    shape = MaterialTheme.shapes.medium,
    offset = DpOffset(0.dp, 16.dp)
) {
    RecipeDropdownItem(text = "Поделиться", icon = Icons.Default.Share) { onDismissRequest() }
    RecipeDropdownItem(text = "Удалить", icon = Icons.Default.Delete) { delete() }
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