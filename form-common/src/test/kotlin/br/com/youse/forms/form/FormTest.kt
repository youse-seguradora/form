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

import br.com.youse.forms.form.models.ObservableValue
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.ValidationType
import br.com.youse.forms.validators.Validator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail


class FormTest {
    companion object {
        private const val EMAIL_ID = 1
        private const val PASSWORD_ID = 2
        private const val AGE_ID = 3
        private const val VALID_EMAIL = "some_email_@some_domain.com"
        private const val VALID_PASSWORD = "12345678"
        private const val MIN_AGE_VALUE = 21
        private const val MAX_AGE_VALUE = 100

        private val VALID_EMAIL_TYPE: ValidationType = object : ValidationType {}
        private val VALID_PASSWORD_TYPE: ValidationType = object : ValidationType {}
        private val MIN_VALUE_TYPE: ValidationType = object : ValidationType {}
        private val MAX_VALUE_TYPE: ValidationType = object : ValidationType {}

        val INVALID_EMAIL_MESSAGE = ValidationMessage("input is not VALID_EMAIL", VALID_EMAIL_TYPE)
        val INVALID_PASSWORD_MESSAGE = ValidationMessage("input is not VALID_PASSWORD", VALID_PASSWORD_TYPE)
        val TOO_LARGE_MESSAGE = ValidationMessage("input is too large", MAX_VALUE_TYPE)
        val TOO_SMALL_MESSAGE = ValidationMessage("input is too small", MIN_VALUE_TYPE)
    }


    private val emailValidators: List<Validator<String>> = listOf(object : Validator<String> {
        override fun isValid(input: String): Boolean {
            return VALID_EMAIL == input
        }

        override fun validationMessage(): ValidationMessage {
            return INVALID_EMAIL_MESSAGE
        }
    })

    private val passwordValidators: List<Validator<CharSequence>> = listOf(object : Validator<CharSequence> {
        override fun isValid(input: CharSequence): Boolean {
            return VALID_PASSWORD == input
        }

        override fun validationMessage(): ValidationMessage {
            return INVALID_PASSWORD_MESSAGE
        }
    })

    private val ageValidators: List<Validator<Int>> = listOf(object : Validator<Int> {

        override fun isValid(input: Int): Boolean {
            return input >= MIN_AGE_VALUE
        }

        override fun validationMessage(): ValidationMessage {
            return TOO_SMALL_MESSAGE
        }

    }, object : Validator<Int> {

        override fun isValid(input: Int): Boolean {
            return input <= MAX_AGE_VALUE
        }

        override fun validationMessage(): ValidationMessage {
            return TOO_LARGE_MESSAGE
        }

    })

    @Test
    fun shouldValidateAllTheTime() {

        val emailObservable = ObservableValue("foo")
        val passwordObservable = ObservableValue("bar")
        val ageObservable = ObservableValue(MIN_AGE_VALUE - 1)

        val validationsList = listOf(Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
                Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE)))

        Form.Builder<Int>(ValidationStrategy.ALL_TIME)
                .addFieldValidations(EMAIL_ID, emailObservable, emailValidators)
                .addFieldValidations(PASSWORD_ID, passwordObservable, passwordValidators)
                .addFieldValidations(AGE_ID, ageObservable, ageValidators)
                .setFieldValidationListener(object : IForm.FieldValidationChange<Int> {
                    var index = 0
                    override fun onFieldValidationChange(key: Int, validations: List<ValidationMessage>) {
                        assertEquals(validationsList[index].first, key)
                        assertEquals(validationsList[index].second, validations)

                        index++
                    }
                })
                .setFormValidationListener(object : IForm.FormValidationChange {
                    var count = 0
                    override fun onFormValidationChange(isValid: Boolean) {
                        if (count > 0) {
                            fail()
                        }
                        count++
                    }
                })
                .setValidSubmitListener(object : IForm.ValidSubmit<Int> {
                    override fun onValidSubmit(fields: List<Pair<Int, Any?>>) {
                        fail()

                    }
                })
                .setSubmitFailedListener(object : IForm.SubmitFailed<Int> {
                    override fun onSubmitFailed(validations: List<Pair<Int, List<ValidationMessage>>>) {
                        assertEquals(validationsList, validations)
                    }
                })
                .build()

    }

    @Test
    fun shouldExecuteValidationAfterSubmit() {
        val emailObservable = ObservableValue("foo")
        val passwordObservable = ObservableValue("bar")
        val ageObservable = ObservableValue(MIN_AGE_VALUE - 1)

        var validate = false
        var onFieldChangeCounter = 0
        var onFormValidationChangeCounter = 0
        var onValidSubmitCounter = 0
        var onFailedSubmitCounter = 0

        val form = Form.Builder<Int>()
                .addFieldValidations(EMAIL_ID, emailObservable, emailValidators)
                .addFieldValidations(PASSWORD_ID, passwordObservable, passwordValidators)
                .addFieldValidations(AGE_ID, ageObservable, ageValidators)
                .setFieldValidationListener(object : IForm.FieldValidationChange<Int> {

                    override fun onFieldValidationChange(key: Int, validations: List<ValidationMessage>) {
                        if (!validate) {
                            fail()
                        }
                        onFieldChangeCounter++
                    }
                })
                .setFormValidationListener(object : IForm.FormValidationChange {

                    override fun onFormValidationChange(isValid: Boolean) {
                        if (!validate) {
                            fail()
                        }
                        onFormValidationChangeCounter++
                    }
                })
                .setValidSubmitListener(object : IForm.ValidSubmit<Int> {
                    override fun onValidSubmit(fields: List<Pair<Int, Any?>>) {
                        if (!validate) {
                            fail()
                        }
                        onValidSubmitCounter++
                    }
                })
                .setSubmitFailedListener(object : IForm.SubmitFailed<Int> {
                    override fun onSubmitFailed(validations: List<Pair<Int, List<ValidationMessage>>>) {
                        if (!validate) {
                            fail()
                        }
                        onFailedSubmitCounter++
                    }
                })
                .build()

        emailObservable.value = "bar"
        passwordObservable.value = "foo"
        ageObservable.value = MAX_AGE_VALUE + 1
        assertEquals(onFieldChangeCounter, 0)
        assertEquals(onFormValidationChangeCounter, 0)
        assertEquals(onValidSubmitCounter, 0)
        assertEquals(onFailedSubmitCounter, 0)

        validate = true
        form.doSubmit()


        assertEquals(onFieldChangeCounter, 3)
        assertEquals(onFormValidationChangeCounter, 1)
        assertEquals(onValidSubmitCounter, 0)
        assertEquals(onFailedSubmitCounter, 1)

        emailObservable.value = VALID_EMAIL
        passwordObservable.value = VALID_PASSWORD
        ageObservable.value = MIN_AGE_VALUE

        assertEquals(onFieldChangeCounter, 6)
        assertEquals(onFormValidationChangeCounter, 2)
        assertEquals(onValidSubmitCounter, 0)
        assertEquals(onFailedSubmitCounter, 1)

        form.doSubmit()

        assertEquals(onValidSubmitCounter, 1)
        assertEquals(onFailedSubmitCounter, 1)
    }

    @Test
    fun shouldCallOnFieldValidationChangeDueFieldValidation() {
        val emailObservable = ObservableValue("foo")
        val firstMessage = "first error message"
        val secondMessage = "second error message"
        val emailValidators = listOf(object : Validator<CharSequence> {
            override fun validationMessage(): ValidationMessage {
                return ValidationMessage(firstMessage, VALID_EMAIL_TYPE)
            }

            override fun isValid(input: CharSequence): Boolean {
                return input.isNotEmpty()
            }
        }, object : Validator<CharSequence> {
            override fun validationMessage(): ValidationMessage {
                return ValidationMessage(secondMessage, VALID_EMAIL_TYPE)
            }

            override fun isValid(input: CharSequence): Boolean {
                return input.contains("@")
            }
        })

        var lastMesasges = listOf<ValidationMessage>()

        val form = Form.Builder<Int>()
                .addFieldValidations(EMAIL_ID, emailObservable, emailValidators)
                .setFieldValidationListener(object : IForm.FieldValidationChange<Int> {

                    override fun onFieldValidationChange(key: Int, validations: List<ValidationMessage>) {
                        lastMesasges = validations
                    }
                })
                .build()

        emailObservable.value = ""

        form.doSubmit()

        assertEquals(lastMesasges.first().message, firstMessage)

        emailObservable.value = " "

        assertEquals(lastMesasges.first().message, secondMessage)

        emailObservable.value = "@"

        assertTrue(lastMesasges.isEmpty())

    }
}