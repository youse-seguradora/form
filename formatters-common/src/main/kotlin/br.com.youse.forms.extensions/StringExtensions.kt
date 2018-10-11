package br.com.youse.forms.extensions

fun String.toDigitsOnly(): String {
    val digitsOnlyRegex = "[^0-9]".toRegex()
    return replace(digitsOnlyRegex, "")
}

fun String.isDigitsOnly(): Boolean {
    return this == toDigitsOnly()
}