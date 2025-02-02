package org.example.project.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import kotlinx.coroutines.flow.MutableStateFlow
import org.example.project.data.network.model.RecipeCreateRequest
import org.example.project.presentation.components.StepTextField
import org.example.project.presentation.util.StepState
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
    val steps by remember { mutableStateOf(
        SnapshotStateList<RichTextState>()
    ) }
    val empty = rememberRichTextState()
    var focusIndex by remember { mutableStateOf<Int?>(null) }
    val listState = rememberLazyListState()

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(state = listState, verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(16.dp)) {
            item {
                CenterAlignedTopAppBar(title = {
                    Text(
                        "Создать рецепт",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                    modifier = Modifier.padding(bottom = 16.dp),
                    navigationIcon = {
                        IconButton(onClick = onCreated) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Back"
                            )
                        }
                    })
            }
            item {
                TextField(
                    current.name,
                    onValueChange = { current = current.copy(name = it) },
                    placeholder = { Text("Название") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
            }
            itemsIndexed(steps) { index, state ->
                StepTextField(
                    richTextState = state, onRemove = { steps.removeAt(index) },
                    isFocused = focusIndex == index, changeFocus = {
                        if (it) { focusIndex = index }
                    }
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            steps.add(empty.copy())
                            println(steps)
                        }
                    ) {
                        Text("Добавить шаг")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {}
                    ) {
                        Text("Добавить фото")
                    }

                }
            }

        }
        AnimatedContent(listState.lastScrolledBackward || !listState.canScrollBackward, modifier =
        Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            .padding(bottom = 32.dp).align(
                Alignment.BottomCenter
            )
        ) {
            if (it) {
                Button(
                    modifier = Modifier.fillMaxWidth().height(ButtonDefaults.MinHeight * 1.25f),
                    onClick = {
                        val merged = steps.map { step -> step.toMarkdown() }.reduce { a, b -> "$a\n$b" }
                        viewModel.addRecipe(current.copy(text = merged), onCreated)
                    },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}