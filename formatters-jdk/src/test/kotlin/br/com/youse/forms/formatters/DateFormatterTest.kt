package br.com.youse.forms.formatters

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class DateFormatterTest {

    @Test
    fun shouldFormatDate() {
        val locale = Locale("PT", "BR")
        val formatter = DateFormatter("dd/MM/yyyy", locale)
        val date = Calendar.getInstance(locale).apply {
            set(Calendar.YEAR, 2018)
            set(Calendar.MONTH, 5)
            set(Calendar.DAY_OF_MONTH, 30)
        }.time

        assertEquals(formatter.format(date), "30/06/2018")
    }
}