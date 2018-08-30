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


// 00:00
class HoursFormatter(val divider: String) : TextFormatter {
    private val digitsOnlyRegex = "[^0-9]".toRegex()

    override fun getCursorPosition(previous: CharSequence, input: CharSequence, output: CharSequence) = output.length

    override fun format(input: CharSequence): String {
        val clearText = input.toDigitsOnly()

        return when (clearText.length) {
            0, 1 -> clearText
            2, 3, 4 -> clearText.substring(0, 2) + divider + clearText.substring(2)
            else -> clearText.substring(0, 2) + divider + clearText.substring(2, 4)
        }
    }

    private fun CharSequence.toDigitsOnly(): String {
        return replace(digitsOnlyRegex, "")
    }
}
