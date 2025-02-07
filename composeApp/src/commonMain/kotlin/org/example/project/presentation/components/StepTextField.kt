package org.example.project.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults

@Composable
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
fun StepTextField(
    richTextState: RichTextState,
    headline: Pair<String, (String) -> Unit>,
    onRemove: () -> Unit,
    isFocused: Boolean,
    changeFocus: (Boolean) -> Unit
) {
    Box(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(headline.first, onValueChange = { headline.second(it) }, modifier = Modifier.height(
                TextFieldDefaults.MinHeight
            ).weight(1f), placeholder = { Text("Заголовок") }, shape = MaterialTheme.shapes.medium, colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent
            ), singleLine = true)
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, contentDescription = "remove item")
            }
        }
        RichTextEditor(
            modifier = Modifier.padding(top = TextFieldDefaults.MinHeight).fillMaxSize().onFocusChanged {
                changeFocus(it.isFocused)
            },
            state = richTextState,
            colors = RichTextEditorDefaults.richTextEditorColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            minLines = 5,
            shape = MaterialTheme.shapes.medium,
            placeholder = { Text("Текст") },
            supportingText = {
                AnimatedVisibility(isFocused) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MaterialTheme.typography.run {
                            Button(
                                contentPadding = PaddingValues(4.dp),
                                shape = MaterialTheme.shapes.medium,
                                colors = if (richTextState.currentSpanStyle.fontWeight == FontWeight.Bold)
                                    ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                                onClick = {
                                    richTextState.toggleSpanStyle(
                                        spanStyle = SpanStyle(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                })
                            { Text("B", style = headlineSmall.copy(fontWeight = FontWeight.Bold)) }
                            Button(
                                contentPadding = PaddingValues(4.dp),
                                shape = MaterialTheme.shapes.medium,
                                colors = if (richTextState.currentSpanStyle.fontStyle == FontStyle.Italic) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                                onClick = {
                                    richTextState.toggleSpanStyle(spanStyle = SpanStyle(fontStyle = FontStyle.Italic))
                                }) {
                                Text(
                                    "I",
                                    style = headlineSmall.copy(fontStyle = FontStyle.Italic)
                                )
                            }
                            Button(
                                contentPadding = PaddingValues(4.dp),
                                shape = MaterialTheme.shapes.medium,
                                colors = if (richTextState.currentSpanStyle.fontSize == headlineMedium.fontSize) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                                onClick = { richTextState.toggleSpanStyle(spanStyle = headlineMedium.toSpanStyle()) }) {
                                Text(
                                    "H1",
                                    style = headlineSmall
                                )
                            }
                            Button(
                                contentPadding = PaddingValues(4.dp),
                                shape = MaterialTheme.shapes.medium,
                                colors = if (richTextState.currentSpanStyle.fontSize == headlineSmall.fontSize) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(),
                                onClick = { richTextState.toggleSpanStyle(spanStyle = headlineSmall.toSpanStyle()) }) {
                                Text(
                                    "H2",
                                    style = headlineSmall
                                )
                            }
                        }
                    }
                } }
        )
    }
}