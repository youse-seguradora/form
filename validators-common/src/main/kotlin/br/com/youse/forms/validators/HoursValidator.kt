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
package br.com.youse.forms.validators

import br.com.youse.forms.validators.ValidationTypes.Companion.HOUR_FORMAT

class HoursValidator(val message: String, val divider: String) : Validator<String> {
    override fun validationMessage(): ValidationMessage {
        return ValidationMessage(message = message, validationType = HOUR_FORMAT)
    }

    override fun isValid(input: String): Boolean {
        return try {
            val parts = input.split(divider)
            val hours = parts.firstOrNull()?.toInt() ?: -1
            val minutes = parts.lastOrNull()?.toInt() ?: -1
            (parts.size == 2
                    && hours >= 0
                    && hours <= 23
                    && minutes >= 0
                    && minutes <= 59)
        } catch (e: Throwable) {
            false
        }
    }
}