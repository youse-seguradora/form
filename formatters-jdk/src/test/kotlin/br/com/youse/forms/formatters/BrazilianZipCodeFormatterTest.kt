package br.com.youse.forms.formatters

import kotlin.test.Test
import kotlin.test.assertEquals

class BrazilianZipCodeFormatterTest {

    @Test
    fun shouldFormat() {
        val formatter = BrazilianZipCodeFormatter()

        assertEquals(formatter.format(""), "")
        assertEquals(formatter.format("0"), "0")
        assertEquals(formatter.format("01"), "01")
        assertEquals(formatter.format("012"), "012")
        assertEquals(formatter.format("0123"), "0123")
        assertEquals(formatter.format("01234"), "01234-")
        assertEquals(formatter.format("01234-"), "01234-")
        assertEquals(formatter.format("012345"), "01234-5")
        assertEquals(formatter.format("0123456"), "01234-56")
        assertEquals(formatter.format("01234567"), "01234-567")
        assertEquals(formatter.format("012345678"), "01234-567")
        assertEquals(formatter.format("0123456789"), "01234-567")

    }
}