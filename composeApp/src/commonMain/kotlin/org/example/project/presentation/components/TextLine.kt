package org.example.project.presentation.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun TextLine(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorText: String = "",
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    placeholderText: String = "Placeholder",
    prefix: String? = null,

    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    modifier: Modifier = Modifier
) {
  var isFocused by remember { mutableStateOf(false) }
  val surfaceBright = MaterialTheme.colorScheme.primary
  val primary = MaterialTheme.colorScheme.primary
  val animProgress by
      animateFloatAsState(
          if (isFocused) 1f else 0f, animationSpec = tween(200, easing = EaseOutCubic))
  val animColor = primary.copy(animProgress).compositeOver(surfaceBright)
  BasicTextField(
      enabled = enabled,
      keyboardOptions = keyboardOptions,
      keyboardActions = keyboardActions,
      visualTransformation = visualTransformation,
      modifier =
          modifier.padding(vertical = 12.dp).height(48.dp).onFocusChanged {
            isFocused = it.isFocused
          },
      textStyle =
          MaterialTheme.typography.headlineSmall.copy(
              color = MaterialTheme.colorScheme.onBackground),
      value = value,
      onValueChange = { it: TextFieldValue -> onValueChange(it) },
      cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
      decorationBox = { innerTextField ->
        Box(
            modifier =
                Modifier.drawWithContent {
                  drawRect(
                      Brush.linearGradient(
                          0f to surfaceBright, 0.85f to surfaceBright, 1f to Color.Transparent),
                      topLeft = Offset(0f, size.height - 1.dp.toPx()),
                      size = Size(size.width, 2.dp.toPx()))
                  drawRect(
                      Brush.linearGradient(
                          0f to animColor, animProgress to animColor, 1f to Color.Transparent),
                      topLeft = Offset(0f, size.height - 1.dp.toPx()),
                      size = Size(size.width, 3.dp.toPx()))
                  drawContent()
                }) {
              Row {
                  if (prefix != null) {
                      Text(prefix,
                          style =
                          MaterialTheme.typography.headlineSmall.copy(
                              MaterialTheme.colorScheme.onBackground
                          ))
                  }
                  Box {
                      innerTextField()
                      if (value.text.isEmpty()) {
                          Text(
                              placeholderText,
                              color = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                              style =
                              MaterialTheme.typography.headlineSmall.copy(
                                  MaterialTheme.colorScheme.onBackground
                              )
                          )
                      }
                  }
                  if (trailingIcon != null) {
                      trailingIcon()
                  }
              }
            }
      })
}
