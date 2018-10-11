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
package br.com.youse.forms.form

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationType
import br.com.youse.forms.validators.Validator


const val EMAIL_ID = 1
const val PASSWORD_ID = 2
const val AGE_ID = 3
const val VALID_EMAIL = "some_email_@some_domain.com"
const val VALID_PASSWORD = "12345678"
const val MIN_AGE_VALUE = 21
const val MAX_AGE_VALUE = 100

val VALID_EMAIL_TYPE: ValidationType = object : ValidationType {}
val VALID_PASSWORD_TYPE: ValidationType = object : ValidationType {}
val MIN_VALUE_TYPE: ValidationType = object : ValidationType {}
val MAX_VALUE_TYPE: ValidationType = object : ValidationType {}

val INVALID_EMAIL_MESSAGE = ValidationMessage("input is not VALID_EMAIL", VALID_EMAIL_TYPE)
val INVALID_PASSWORD_MESSAGE = ValidationMessage("input is not VALID_PASSWORD", VALID_PASSWORD_TYPE)
val TOO_LARGE_MESSAGE = ValidationMessage("input is too large", MAX_VALUE_TYPE)
val TOO_SMALL_MESSAGE = ValidationMessage("input is too small", MIN_VALUE_TYPE)

val emailValidators: List<Validator<String>> = listOf(object : Validator<String> {
    override fun isValid(input: String?): Boolean {
        return VALID_EMAIL == input
    }

    override fun validationMessage(): ValidationMessage {
        return INVALID_EMAIL_MESSAGE
    }
})

val passwordValidators: List<Validator<String>> = listOf(object : Validator<String> {
    override fun isValid(input: String?): Boolean {
        return VALID_PASSWORD == input
    }

    override fun validationMessage(): ValidationMessage {
        return INVALID_PASSWORD_MESSAGE
    }
})

val ageValidators: List<Validator<Int>> = listOf(object : Validator<Int> {

    override fun isValid(input: Int?): Boolean {
        return input != null && input >= MIN_AGE_VALUE
    }

    override fun validationMessage(): ValidationMessage {
        return TOO_SMALL_MESSAGE
    }

}, object : Validator<Int> {

    override fun isValid(input: Int?): Boolean {
        return input != null && input <= MAX_AGE_VALUE
    }

    override fun validationMessage(): ValidationMessage {
        return TOO_LARGE_MESSAGE
    }

})
