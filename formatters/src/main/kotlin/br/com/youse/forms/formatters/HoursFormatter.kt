package br.com.youse.forms.formatters


// 00:00
class HoursFormatter(val divider: String) : TextFormatter {
    private val digitsOnlyRegex = "[^0-9]".toRegex()

    override fun getCursorPosition(previous: CharSequence, input: CharSequence, output: CharSequence) = output.length

    override fun format(input: CharSequence): CharSequence {
        val clearText = input.toDigitsOnly()

        return when (clearText.length) {
            0, 1 -> clearText
            2, 3, 4 -> clearText.substring(0, 2) + divider + clearText.substring(2)
            else -> clearText.substring(0, 2) + divider + clearText.substring(2, 4)
        }
    }

    private fun CharSequence.toDigitsOnly(): String {
        return replace(digitsOnlyRegex, "")
    }
}
