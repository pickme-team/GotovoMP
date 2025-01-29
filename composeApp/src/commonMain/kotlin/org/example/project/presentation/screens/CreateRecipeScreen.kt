package org.example.project.presentation.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorColors
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults
import org.example.project.data.network.model.RecipeCreateRequest
import org.example.project.viewModels.PersonalVM

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun CreateRecipeScreen(
    onCreated: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PersonalVM = viewModel(),
) {
    var current by remember {
        mutableStateOf(
            RecipeCreateRequest(
                "", "", emptyList(), emptyList()
            )
        )
    }
    val listState = rememberLazyListState()
    Box(modifier = Modifier.fillMaxSize()) {
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
            item {
                val state = rememberRichTextState()
                RichTextEditor(
                    modifier = Modifier.fillMaxWidth(),
                    state = state,
                    colors = RichTextEditorDefaults.richTextEditorColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    minLines = 5,
                    shape = MaterialTheme.shapes.medium,
                    supportingText = { FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MaterialTheme.typography.run {
                            Button(contentPadding = PaddingValues(4.dp), shape = MaterialTheme.shapes.medium, colors = if (state.currentSpanStyle.fontWeight == FontWeight.Bold) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(), onClick = { state.toggleSpanStyle(spanStyle = SpanStyle(fontWeight = FontWeight.Bold)) }) { Text("B", style = headlineSmall.copy(fontWeight = FontWeight.Bold)) }
                            Button(contentPadding = PaddingValues(4.dp), shape = MaterialTheme.shapes.medium, colors = if (state.currentSpanStyle.fontStyle == FontStyle.Italic) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(), onClick = { state.toggleSpanStyle(spanStyle = SpanStyle(fontStyle = FontStyle.Italic)) }) { Text("I", style = headlineSmall.copy(fontStyle = FontStyle.Italic)) }
                            Button(contentPadding = PaddingValues(4.dp), shape = MaterialTheme.shapes.medium, colors = if (state.currentSpanStyle.fontSize == headlineMedium.fontSize) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(), onClick = { state.toggleSpanStyle(spanStyle = headlineMedium.toSpanStyle()) }) { Text("H1", style = headlineSmall) }
                            Button(contentPadding = PaddingValues(4.dp), shape = MaterialTheme.shapes.medium, colors = if (state.currentSpanStyle.fontSize == headlineSmall.fontSize) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(), onClick = { state.toggleSpanStyle(spanStyle = headlineSmall.toSpanStyle()) }) { Text("H2", style = headlineSmall) }
                        }
                    } }
                )
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
                    onClick = { viewModel.addRecipe(current, onCreated) },
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Сохранить")
                }
            }
        }
    }
}