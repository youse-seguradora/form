package br.com.youse.forms.formatters

import br.com.youse.forms.formatters.Formatter
import java.text.NumberFormat
import java.util.*

class CurrencyFormatter(private val locale: Locale) : Formatter<Number> {
    override fun format(input: Number): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance(locale)
        return currencyFormatter.format(input)
    }
}