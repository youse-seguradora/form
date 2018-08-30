package br.com.youse.forms.formatters

import br.com.youse.forms.extensions.toDigitsOnly


// NOTE: Only supports PT_BR locale.
class ZipCodeFormatter : Formatter<String> {

    private val formatter: Formatter<String> = BrazilianZipCodeFormatter()

    override fun format(input: String): String = formatter.format(input)

}

// 04526-001
class BrazilianZipCodeFormatter : Formatter<String> {
    override fun format(input: String): String {
        val clearText = input.toDigitsOnly()
        return when (clearText.length) {
            in 0..4 -> input
            in 4..8 ->
                clearText.substring(0, 5) + "-" + clearText.substring(5)
            else ->
                clearText.substring(0, 5) + "-" + clearText.substring(5, 8)
        }
    }

}