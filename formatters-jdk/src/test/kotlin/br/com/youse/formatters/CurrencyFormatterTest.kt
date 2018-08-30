package br.com.youse.formatters

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyFormatterTest() {


    @Test
    fun shouldFormatToBrazilianReaLs() {
        val formatter = CurrencyFormatter(Locale("pt", "BR"))

        assertEquals(formatter.format(0.09), "R$ 0,09")
        assertEquals(formatter.format(0.9), "R$ 0,90")
        assertEquals(formatter.format(9), "R$ 9,00")
        assertEquals(formatter.format(1000), "R$ 1.000,00")
        assertEquals(formatter.format(1000_000), "R$ 1.000.000,00")
    }

    @Test
    fun shouldFormatToUSDollar() {
        val formatter = CurrencyFormatter(Locale("en", "US"))

        assertEquals(formatter.format(0.09), "$0.09")
        assertEquals(formatter.format(0.9), "$0.90")
        assertEquals(formatter.format(9), "$9.00")
        assertEquals(formatter.format(1000), "$1,000.00")
        assertEquals(formatter.format(1000_000), "$1,000,000.00")
    }


}