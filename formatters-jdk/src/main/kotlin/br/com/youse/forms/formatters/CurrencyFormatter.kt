package br.com.youse.forms.formatters

import java.text.NumberFormat
import java.util.*

class CurrencyFormatter(private val locale: Locale) : Formatter<Number> {
    private val formatter = NumberFormat.getCurrencyInstance(locale)
    override fun format(input: Number): String {
        return formatter.format(input)
    }
}