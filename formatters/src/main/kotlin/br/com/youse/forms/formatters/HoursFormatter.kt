package br.com.youse.forms.formatters

import java.util.regex.Pattern

// 00:00
class HoursFormatter(val splitter: String) : TextFormatter {

    override fun getCursorPosition(previous: String, input: String, output: String) = output.length

    override fun format(inputText: String): String {
        val clearText = inputText.toDigitsOnly()

        return when (clearText.length) {
            0, 1, 2 -> clearText
            3, 4 -> clearText.substring(0, 2) + splitter + clearText.substring(2)
            else -> clearText.substring(0, 2) + splitter + clearText.substring(2, 4)
        }
    }

}

private fun String.toDigitsOnly(): String {
    return this.replace(Pattern.compile("[^0-9]").toRegex(), "")
}