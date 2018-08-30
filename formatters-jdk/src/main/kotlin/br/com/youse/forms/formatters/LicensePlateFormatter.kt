package br.com.youse.forms.formatters

// NOTE: Only supports PT_BR
class LicensePlateFormatter : Formatter<String> {

    private val formatter = BrazilianLicensePlateFormatter()

    override fun format(input: String): String = formatter.format(input)
}

// XXX-DDDD
class BrazilianLicensePlateFormatter : Formatter<String> {
    private val divider: String = "-"
    override fun format(input: String): String {
        val clearText = input.replace(divider, "")
        return when (clearText.length) {
            in 0..3 ->
                clearText
            in 3..7 ->
                clearText.substring(0, 3) + divider + clearText.substring(3)
            else ->
                clearText.substring(0, 3) + divider + clearText.substring(3, 7)
        }


    }
}

