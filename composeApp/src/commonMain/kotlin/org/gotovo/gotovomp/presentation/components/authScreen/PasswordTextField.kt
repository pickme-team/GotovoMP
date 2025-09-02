package org.gotovo.gotovomp.presentation.components.authScreen

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.gotovo.gotovomp.presentation.components.common.TextLine
import org.gotovo.gotovomp.util.Setter

data class PasswordTextLineState(
    val text: Setter<TextFieldValue>,
    val isVisible: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)


@Composable
fun PasswordVisibilityIcon(
    isVisible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animationProgress by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0.5f,
        animationSpec = tween(durationMillis = 200, easing = EaseOutCubic),
        label = "visibilityAnimation"
    )

    IconButton(
        onClick = onToggle,
        modifier = modifier.size(24.dp)
    ) {
        Icon(
            imageVector = if (isVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
            contentDescription = if (isVisible) "Скрыть пароль" else "Показать пароль",
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(animationProgress)
        )
    }
}

@Composable
fun PasswordTextLine(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorText: String = "",
    placeholderText: String = "Пароль",
    modifier: Modifier = Modifier
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    TextLine(
        value = value,
        onValueChange = onValueChange,
        enabled = enabled,
        isError = isError,
        errorText = errorText,
        placeholderText = placeholderText,
        visualTransformation = if (isPasswordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            PasswordVisibilityIcon(
                isVisible = isPasswordVisible,
                onToggle = { isPasswordVisible = !isPasswordVisible },
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password
        ),
        modifier = modifier
    )
}