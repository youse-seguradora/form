package br.com.youse.forms.formatters

import java.text.SimpleDateFormat
import java.util.*

class DateFormatter(pattern: String, locale: Locale) : Formatter<Date> {

    private val dateFormatter = SimpleDateFormat(pattern, locale)

    override fun format(input: Date): String {
        return dateFormatter.format(input)
    }
}