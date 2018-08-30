package br.com.youse.forms.extensions

fun CharSequence.toDigitsOnly(): String {
    val digitsOnlyRegex = "[^0-9]".toRegex()
    return replace(digitsOnlyRegex, "")
}