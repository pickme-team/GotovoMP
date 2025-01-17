package org.example.project


fun String.nullIfBlank(): String? = if (isBlank() || isEmpty()) null else this