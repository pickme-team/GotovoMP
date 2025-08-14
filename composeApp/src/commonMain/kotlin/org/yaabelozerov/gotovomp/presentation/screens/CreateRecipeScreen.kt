package org.yaabelozerov.gotovomp.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Badge
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import io.github.aakira.napier.Napier
import org.yaabelozerov.gotovomp.data.network.model.IngredientCreateRequest
import org.yaabelozerov.gotovomp.data.network.model.RecipeCreateRequest
import org.yaabelozerov.gotovomp.presentation.components.DropdownTextField
import org.yaabelozerov.gotovomp.presentation.components.StepTextField
import org.yaabelozerov.gotovomp.util.toIntOrStay
import org.yaabelozerov.gotovomp.util.transformQuantityFromDto
import org.yaabelozerov.gotovomp.util.transformQuantityToDto
import org.yaabelozerov.gotovomp.viewModels.PersonalVM
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateRecipeScreen(
    onCreated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PersonalVM,
) {
    var current by remember {
        mutableStateOf(
            RecipeCreateRequest(
                "", "", emptyList(), emptyList()
            )
        )
    }
    val uiState by viewModel.state.collectAsState()
    val steps by remember {
        mutableStateOf(
            SnapshotStateList<Pair<String, RichTextState>>()
        )
    }
    val empty = rememberRichTextState()
    var focusIndex by remember { mutableStateOf<Int?>(null) }
    val listState = rememberLazyListState()
    var ingredientsOpen by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().consumeWindowInsets(WindowInsets.systemBars)) {
        LazyColumn(state = listState, contentPadding = PaddingValues(16.dp)) {
            item {
                CenterAlignedTopAppBar(title = {
                    TextField(
                        current.name,
                        onValueChange = { current = current.copy(name = it) },
                        placeholder = {
                            Text(
                                "Название", style = MaterialTheme.typography.headlineSmall
                            )
                        },
                        textStyle = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                }, modifier = Modifier.padding(bottom = 16.dp), navigationIcon = {
                    IconButton(onClick = onCreated) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back"
                        )
                    }
                })
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium).clickable {
                        ingredientsOpen = true
                    }.padding(vertical = 4.dp).padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Ингридиенты",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Badge(backgroundColor = MaterialTheme.colorScheme.primary) {
                        Text(
                            uiState.currentIngredients.size.toString(),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { ingredientsOpen = true }) {
                        Icon(
                            Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = "to ingredients"
                        )
                    }
                }
            }
            itemsIndexed(steps) { index, state ->
                StepTextField(
                    modifier = Modifier.padding(top = 8.dp),
                    index = index,
                    richTextState = state.second,
                    headline = state.first to { steps[index] = state.copy(first = it) },
                    onRemove = { steps.removeAt(index) },
                    isFocused = focusIndex == index,
                    changeFocus = {
                        if (it) {
                            focusIndex = index
                        }
                    })
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(modifier = Modifier.weight(1f), onClick = {
                        steps.add("" to empty.copy())
                        Napier.d { steps.toString() }
                    }) {
                        Text("Добавить шаг")
                    }
                    Button(modifier = Modifier.weight(1f), onClick = {}) {
                        Text("Добавить фото")
                    }

                }
            }

        }
        AnimatedContent(
            listState.lastScrolledBackward || !listState.canScrollBackward,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).padding(bottom = 32.dp)
                .align(
                    Alignment.BottomCenter
                )
        ) {
            if (it) {
                Button(
                    modifier = Modifier.fillMaxWidth().height(ButtonDefaults.MinHeight * 1.25f),
                    onClick = {
                        val merged =
                            steps.mapIndexed { index, step -> step.first.ifBlank { "Шаг ${index + 1}" } + "<br>" + step.second.toMarkdown() }
                                .reduceOrNull { a, b -> "$a%#*8$b" }
                        viewModel.addRecipe(
                            current.copy(
                                text = merged ?: "", ingredients = uiState.currentIngredients
                            ), onCreated
                        )
                        viewModel.cleanIngredients()
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Сохранить")
                }
            }
        }
        AnimatedVisibility(
            ingredientsOpen,
            enter = slideInHorizontally(initialOffsetX = { it }),
            exit = slideOutHorizontally(targetOffsetX = { it })
        ) {
            IngredientScreen(viewModel = viewModel, onBack = { ingredientsOpen = false })
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun IngredientScreen(viewModel: PersonalVM, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val uiState by viewModel.state.collectAsState()
    var showAddUi by remember { mutableStateOf(false) }
    BackHandler(onBack = onBack)
    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SearchBar(
                query = uiState.ingredientQuery to viewModel::updateIngredientQuery,
                showAddUi = showAddUi,
                onShowAddUi = { showAddUi = !showAddUi },
                onBack = onBack,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        item {
            CreateIngredientCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                show = showAddUi,
                onCreated = {
                    viewModel.addIngredient(it)
                })
        }
        item {
            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 4.dp) {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    uiState.currentIngredients.forEachIndexed { index, it ->
                        Chip(
                            onClick = { viewModel.removeIngredientAt(index) },
                            colors = ChipDefaults.chipColors(
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    it.name, modifier = Modifier.padding(8.dp)
                                )
                                Text(
                                    "${it.quantity.toIntOrStay()} ${it.quantityType.transformQuantityFromDto()}",
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        items(uiState.foundIngredients, key = { it }) {
            Card(onClick = {}) {
                Text(it.name)
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: Pair<String, (String) -> Unit>,
    showAddUi: Boolean,
    onShowAddUi: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val fr = remember { FocusRequester() }
    val fm = LocalFocusManager.current
    LaunchedEffect(Unit) {
        fr.requestFocus()
    }
    var isFocused by remember { mutableStateOf(false) }
    val shape by animateDpAsState(if (isFocused) 0.dp else 16.dp)
    TextField(
        query.first,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        ),
        leadingIcon = {
            IconButton(modifier = Modifier.padding(start = 4.dp), onClick = {
                fm.clearFocus()
                query.second("")
                onBack()
            }) { Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "назад") }
        },
        onValueChange = { query.second(it) },
        shape = RoundedCornerShape(shape),
        placeholder = { Text("Поиск", style = MaterialTheme.typography.headlineSmall) },
        singleLine = true,
        keyboardActions = KeyboardActions(onSearch = {
            fm.clearFocus()
        }),
        trailingIcon = {
            IconButton(modifier = Modifier.padding(end = 4.dp), onClick = onShowAddUi) {
                if (showAddUi) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "скрыть")
                } else {
                    Icon(Icons.Default.AddCircle, contentDescription = "добавить")
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        textStyle = MaterialTheme.typography.headlineSmall,
        modifier = modifier.fillMaxWidth().onFocusChanged { isFocused = it.isFocused }
            .focusRequester(fr).padding(horizontal = shape).padding(top = shape.div(2)))
}


@Composable
private fun CreateIngredientCard(
    show: Boolean,
    onCreated: (IngredientCreateRequest) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        show,
        modifier = modifier,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        val quantityOptions = listOf("г.", "мл.", "шт.")

        var name by remember { mutableStateOf("") }
        val quantityType = mutableStateOf(quantityOptions[0])
        var quantity by remember { mutableStateOf(1L) }
        var category by remember { mutableStateOf("") }
        var extra by remember { mutableStateOf("") }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Добавить ингридиент",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                OutlinedTextField(
                    name,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    onValueChange = { name = it },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Название") },
                    singleLine = true
                )
                OutlinedTextField(
                    category,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    onValueChange = { category = it },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Категория") },
                    singleLine = true
                )
                OutlinedTextField(
                    extra,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    onValueChange = { extra = it },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Дополнительно") },
                    singleLine = true
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        quantity.toString(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        onValueChange = { quantity = it.toLongOrNull() ?: 0L },
                        shape = MaterialTheme.shapes.medium,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                        modifier = Modifier.weight(1f, false),
                        leadingIcon = {
                            IconButton(onClick = {
                                quantity = max(quantity - 1, 1)
                            }) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown, contentDescription = "убрать"
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(onClick = { quantity++ }) {
                                Icon(
                                    Icons.Default.KeyboardArrowUp, contentDescription = "добавить"
                                )
                            }
                        })
                    DropdownTextField(
                        label = "Ед. изм.",
                        options = quantityOptions,
                        selectedOption = quantityType,
                        modifier = Modifier.weight(1f)
                    )
                }
                Button(
                    onClick = {
                        onCreated(
                            IngredientCreateRequest(
                                name = name,
                                quantityType = quantityType.value.transformQuantityToDto(),
                                quantity = quantity.toDouble(),
                                category = category,
                                additionalParameters = extra
                            )
                        )
                    },
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.height(OutlinedTextFieldDefaults.MinHeight)
                ) { Text("Сохранить") }
            }
        }
    }
}