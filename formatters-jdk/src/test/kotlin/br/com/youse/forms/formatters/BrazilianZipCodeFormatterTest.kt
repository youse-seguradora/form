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