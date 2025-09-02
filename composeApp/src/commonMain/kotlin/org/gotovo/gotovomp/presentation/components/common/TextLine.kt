package org.gotovo.gotovomp.presentation.components.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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

@Suppress("SuspiciousIndentation")
@Composable
fun TextLine(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorText: String = "",
    supportingText: String = "",
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
    val style = MaterialTheme.typography.titleLarge.copy(
        MaterialTheme.colorScheme.onBackground
    )

  var isFocused by remember { mutableStateOf(false) }
  val surfaceBright = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f)
  val primary = MaterialTheme.colorScheme.primary
  val error = MaterialTheme.colorScheme.error
  val lineColor = if (isError) error else primary
  val animProgress by
      animateFloatAsState(
          if (isFocused || isError) 1f else 0f, animationSpec = tween(200, easing = EaseOutCubic))
  val animColor = lineColor.copy(animProgress).compositeOver(surfaceBright)
    Column {
        BasicTextField(
            enabled = enabled,
            singleLine = true,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            modifier =
            modifier.height(48.dp).onFocusChanged {
                isFocused = it.isFocused
            },
            textStyle = style,
            value = value,
            onValueChange = { it: TextFieldValue -> onValueChange(it) },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            decorationBox = { innerTextField ->
                Box(
                    modifier =
                    Modifier.drawWithContent {
                        drawRect(
                            Brush.linearGradient(
                                0f to surfaceBright, 0.75f to surfaceBright, 1f to Color.Transparent),
                            topLeft = Offset(0f, size.height - 6.dp.toPx()),
                            size = Size(size.width, 2.dp.toPx()))
                        drawRect(
                            Brush.linearGradient(
                                0f to animColor, animProgress to animColor, 1f to Color.Transparent),
                            topLeft = Offset(0f, size.height - 6.dp.toPx()),
                            size = Size(size.width, 3.dp.toPx()))
                        drawContent()
                    }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (prefix != null) {
                            Text(prefix,
                                style = style)
                        }
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            innerTextField()
                            if (value.text.isEmpty()) {
                                Text(
                                    placeholderText,
                                    color = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                                    style = style
                                )
                            }
                        }
                        if (trailingIcon != null) {
                            trailingIcon()
                        }
                    }
                }
            })
        AnimatedVisibility(isError) {
            Text(errorText, color = MaterialTheme.colorScheme.error)
        }
    }
}
