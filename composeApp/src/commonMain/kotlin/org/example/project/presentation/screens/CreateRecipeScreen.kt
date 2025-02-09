package org.example.project.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Badge
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import org.example.project.data.network.model.Ingredient
import org.example.project.data.network.model.RecipeCreateRequest
import org.example.project.presentation.components.StepTextField
import org.example.project.viewModels.PersonalVM

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
    val steps by remember {
        mutableStateOf(
            SnapshotStateList<Pair<String, RichTextState>>()
        )
    }
    val empty = rememberRichTextState()
    var focusIndex by remember { mutableStateOf<Int?>(null) }
    val listState = rememberLazyListState()
    var ingredientsOpen by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(state = listState, contentPadding = PaddingValues(16.dp)) {
            item {
                CenterAlignedTopAppBar(title = {
                    TextField(current.name,
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
                    modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
                        .clickable {
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
                            current.ingredients.size.toString(),
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
                StepTextField(modifier = Modifier.padding(top = 8.dp),
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
                        println(steps)
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
                                .reduce { a, b -> "$a\n$b" }
                        viewModel.addRecipe(current.copy(text = merged), onCreated)
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Сохранить")
                }
            }
        }
        if (ingredientsOpen) IngredientScreen(
            current.ingredients,
            onBack = { ingredientsOpen = false })
    }
}

@Composable
fun IngredientScreen(current: List<Ingredient>, onBack: () -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        item {
            SearchBar(onBack = onBack)
        }
        items(current, key = { it }) {
            Card(onClick = {}) {
                Text(it.name)
            }
        }
    }
}

@Composable
private fun SearchBar(onBack: () -> Unit) {
    var query by remember { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }
    val shape by animateDpAsState(if (isFocused) 0.dp else 16.dp)
    TextField(
        query,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        ),
        leadingIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "назад") } },
        onValueChange = { query = it },
        shape = RoundedCornerShape(shape),
        modifier = Modifier.fillMaxWidth().onFocusChanged { isFocused = it.isFocused }.padding(horizontal = shape, vertical = shape.div(2))
    )
}
