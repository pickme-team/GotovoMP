package org.example.project.presentation.util

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInCirc
import androidx.compose.animation.core.EaseInOutQuad
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Modifier.bouncyClickable(
    onClick: () -> Unit = {},
    onHold: () -> Unit = {},
    shrinkSize: Float = 0.925f
) = composed {
    var buttonState by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        if (buttonState) shrinkSize else 1f,
        animationSpec = tween(durationMillis = 100)
    )
    graphicsLayer {
        scaleX = scale
        scaleY = scale
    }.clickable(interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = {
            onClick()
        }).pointerInput(Unit) {
        detectTapGestures(onLongPress = { onHold() }, onTap = { onClick() })
    }.pointerInput(buttonState) {
        awaitPointerEventScope {
            buttonState = if (buttonState) {
                waitForUpOrCancellation()
                false
            } else {
                awaitFirstDown(false)
                true
            }
        }
    }
}
