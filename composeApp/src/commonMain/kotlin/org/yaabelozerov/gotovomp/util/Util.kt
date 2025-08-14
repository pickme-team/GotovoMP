package org.yaabelozerov.gotovomp.util

import androidx.compose.ui.text.input.TextFieldValue


fun String.nullIfBlank(): String? = if (isBlank() || isEmpty()) null else this

object Const {
    val placeholderImages = listOf(
        "https://lobsterfrommaine.com/wp-content/uploads/fly-images/1577/20210517-Pasta-alla-Gricia-with-Lobster3010-1024x576-c.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/a/a3/Eq_it-na_pizza-margherita_sep2005_sml.jpg",
        "https://upload.wikimedia.org/wikipedia/commons/8/86/Gnocchi_di_ricotta_burro_e_salvia.jpg",
        "https://bakesbybrownsugar.com/wp-content/uploads/2019/11/Pecan-Cinnamon-Rolls-84-500x500.jpg",
        "https://bestlah.sg/wp-content/uploads/2024/07/Best-Peranakan-Food-Singapore.jpeg"
    )
}

data class Setter<T>(val value: T, val setter: (T) -> Unit)

operator fun <T> Setter<T>.invoke(value: T) = this.setter(value)

fun <T> T.setter(onChange: (T) -> Unit): Setter<T> = Setter(this, onChange)

operator fun Setter<TextFieldValue>.invoke() = this.value.text

fun Double.toIntOrStay() = if (this % 1 == 0.0) this.toInt() else this

fun String.transformQuantityFromDto(): String {
    val mapRu = mapOf(
        "PCS" to "шт.",
        "GRAMS" to "г.",
        "KGS" to "кг.",
        "L" to "л.",
        "MLS" to "мл.",
    )
    val mapEn = mapOf(
        "PCS" to "pcs.",
        "GRAMS" to "g.",
        "KGS" to "kg.",
        "L" to "l.",
        "MLS" to "ml.",
    )
    return if (getLanguage() == "ru") {
        mapRu[this] ?: this
    } else {
        mapEn[this] ?: this
    }
}

fun String.transformQuantityToDto(): String {
    val mapRu = mapOf(
        "шт." to "PCS",
        "г." to "GRAMS",
        "кг." to "KGS",
        "л." to "L",
        "мл." to "MLS",
    )
    val mapEn = mapOf(
        "pcs." to "PCS",
        "g." to "GRAMS",
        "kg." to "KGS",
        "l." to "L",
        "ml." to "MLS",
    )
    return if (getLanguage() == "ru") {
        mapRu[this] ?: this
    } else {
        mapEn[this] ?: this
    }
}
