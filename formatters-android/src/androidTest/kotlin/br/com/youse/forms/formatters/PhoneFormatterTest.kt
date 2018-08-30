package br.com.youse.forms.formatters

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class PhoneFormatterTest {

    @Test
    fun shouldFormatBrazilianPhoneNumbers() {
        val formatter = PhoneFormatter(Locale("pt", "BR"))

        assertEquals(formatter.format(""), "")

        assertEquals(formatter.format("1899969"), "1899969")

        assertEquals(formatter.format("1899969388"), "(18) 9996-9388")

        assertEquals(formatter.format("18999693880"), "(18) 99969-3880")

        assertEquals(formatter.format("5518999693880"), "55 18 99969-3880")

        assertEquals(formatter.format("+5518999693880"), "+55 18 99969-3880")
    }
}
