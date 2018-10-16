/**
MIT License

Copyright (c) 2018 Youse Seguros

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package br.com.youse.forms.formatters

import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class CurrencyFormatterTest {


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