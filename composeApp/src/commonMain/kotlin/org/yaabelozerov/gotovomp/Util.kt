package org.yaabelozerov.gotovomp

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
