package org.yaabelozerov.gotovomp.presentation.util

sealed interface ValidationState {
    data object Unset : ValidationState
    data object Valid : ValidationState
    data object Invalid : ValidationState
}

fun ValidationState.isError() = this == ValidationState.Invalid
fun ValidationState.isValid() = this == ValidationState.Valid

inline fun String.validation(condition: (String) -> Boolean): ValidationState = when {
    isEmpty() -> ValidationState.Unset
    else -> if (condition(this)) ValidationState.Valid else ValidationState.Invalid
}