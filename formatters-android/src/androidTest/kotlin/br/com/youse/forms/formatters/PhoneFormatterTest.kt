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

class PhoneFormatterTest {

    @Test
    fun shouldFormatBrazilianPhoneNumbers() {
        val formatter = PhoneFormatter(Locale("pt", "BR"))

        assertEquals(formatter.format(""), "")

        assertEquals(formatter.format("1899969"), "1899969")

        assertEquals(formatter.format("189996938"), "189996938")

        assertEquals(formatter.format("1899969388"), "(18) 9996-9388")

        assertEquals(formatter.format("18999693880"), "(18) 99969-3880")

        assertEquals(formatter.format("5518999693880"), "55 18 99969-3880")

        assertEquals(formatter.format("+5518999693880"), "+55 18 99969-3880")
    }
}
