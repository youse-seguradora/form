package br.com.youse.forms.formatters

import java.util.regex.Pattern

// 00:00
class HoursFormatter : TextFormatter {

    override fun getCursorPosition(previous: String, input: String, output: String) = output.length

    override fun format(inputText: String): String {
        val clearText = clearFormatting(inputText)

        return when (clearText.length) {
            0, 1, 2 -> clearText
            3, 4 -> clearText.substring(0, 2) + ":" + clearText.substring(2)
            else -> clearText.substring(0, 2) + ":" + clearText.substring(2, 4)
        }
    }

    private fun clearFormatting(inputText: String) = inputText.toDigitsOnly()
}

private fun String.toDigitsOnly(): String {
    return this.replace(Pattern.compile("[^0-9]").toRegex(), "")
}