package br.com.youse.forms.formatters

import kotlin.test.Test
import kotlin.test.assertEquals

class BrazilianLicensePlateFormatterTest {

    @Test
    fun shouldFormatPlate() {
        val formatter = BrazilianLicensePlateFormatter()

        assertEquals(formatter.format("x"), "x")
        assertEquals(formatter.format("xx"), "xx")
        assertEquals(formatter.format("xxx"), "xxx")
        assertEquals(formatter.format("xxx-"), "xxx")
        assertEquals(formatter.format("xxx-1"), "xxx-1")
        assertEquals(formatter.format("xxx11"), "xxx-11")
        assertEquals(formatter.format("xxx111"), "xxx-111")
        assertEquals(formatter.format("xxx1111"), "xxx-1111")
        assertEquals(formatter.format("xxx11111"), "xxx-1111")
        assertEquals(formatter.format("xxxx11111"), "xxx-x111")
    }
}