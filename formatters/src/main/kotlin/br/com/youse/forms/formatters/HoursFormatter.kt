package br.com.youse.forms.formatters

import java.util.regex.Pattern

// 00:00
class HoursFormatter(val splitter: String) : CharSequenceFormatter {

    override fun getCursorPosition(previous: CharSequence, input: CharSequence, output: CharSequence) = output.length

    override fun format(inputText: CharSequence): CharSequence {
        val clearText = inputText.toDigitsOnly()

        return when (clearText.length) {
            0, 1, 2 -> clearText
            3, 4 -> clearText.substring(0, 2) + splitter + clearText.substring(2)
            else -> clearText.substring(0, 2) + splitter + clearText.substring(2, 4)
        }
    }

}

private fun CharSequence.toDigitsOnly(): String {
    return this.replace(Pattern.compile("[^0-9]").toRegex(), "")
}