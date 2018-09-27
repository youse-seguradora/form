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
package br.com.youse.forms.rxform

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.ValidationType
import br.com.youse.forms.validators.Validator
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlin.test.Test


class RxFormTests {
    private fun <T> getBuilder(submit: Observable<Unit>,
                               strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT): IRxForm.Builder<T> {
        return RxForm.Builder(submit, strategy)
    }

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


    private val submit = PublishSubject.create<Unit>()
    private val emailObservable = PublishSubject.create<String>()
    private val passwordObservable = PublishSubject.create<CharSequence>()
    private val ageObservable = PublishSubject.create<Int>()

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
        val form = getBuilder<Int>(submit, ValidationStrategy.ALL_TIME)
                .addField(EMAIL_ID, emailObservable, emailValidators)
                .addField(PASSWORD_ID, passwordObservable, passwordValidators)
                .addField(AGE_ID, ageObservable, ageValidators)
                .build()

        val fieldsSub = form.onFieldValidationChange().test()
        val formSub = form.onFormValidationChange().test()
        val validSubmitSub = form.onValidSubmit().test()
        val submitFailedSub = form.onSubmitFailed().test()

        emailObservable.onNext("foo")
        passwordObservable.onNext("bar")
        ageObservable.onNext(MIN_AGE_VALUE - 1)


        fieldsSub.assertValues(
                Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
                Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE))
        )

        formSub.assertValue(false)

        validSubmitSub.assertNoValues().assertNoErrors()

    }

    @Test
    fun shouldExecuteValidationAfterSubmit() {
        val form = getBuilder<Int>(submit)
                .addField(EMAIL_ID, emailObservable, emailValidators)
                .addField(PASSWORD_ID, passwordObservable, passwordValidators)
                .addField(AGE_ID, ageObservable, ageValidators)
                .build()

        val fieldsSub = form.onFieldValidationChange().test()
        val formSub = form.onFormValidationChange().test()
        val validSubmitSub = form.onValidSubmit().test()
        val submitFailedSub = form.onSubmitFailed().test()

        emailObservable.onNext("foo")
        passwordObservable.onNext("bar")
        ageObservable.onNext(MIN_AGE_VALUE - 1)
        submit.onNext(Unit)

        fieldsSub.assertValues(
                Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
                Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE))
        )

        formSub.assertValue(false)

        submitFailedSub.assertValue(listOf(
                Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
                Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE))
        ))

        validSubmitSub.assertNoValues().assertNoErrors()

        emailObservable.onNext(VALID_EMAIL)
        passwordObservable.onNext(VALID_PASSWORD)
        ageObservable.onNext(MIN_AGE_VALUE)

        fieldsSub.assertValues(
                Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
                Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE)),
                Pair(EMAIL_ID, listOf()),
                Pair(PASSWORD_ID, listOf()),
                Pair(AGE_ID, listOf())
        )

        formSub.assertValues(false, true)

        submit.onNext(Unit)

        submitFailedSub.assertValues(listOf(
                Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
                Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE))
        ))

        validSubmitSub.assertValue(listOf(
                Pair(EMAIL_ID, VALID_EMAIL),
                Pair(PASSWORD_ID, VALID_PASSWORD),
                Pair(AGE_ID, MIN_AGE_VALUE)
        ))

    }

    @Test
    fun shouldNotValidateBeforeSubmit() {
        val form = getBuilder<Int>(submit)
                .addField(EMAIL_ID, emailObservable, emailValidators)
                .addField(PASSWORD_ID, passwordObservable, passwordValidators)
                .addField(AGE_ID, ageObservable, ageValidators)
                .build()

        val fieldsSub = form.onFieldValidationChange().test()
        val formSub = form.onFormValidationChange().test()
        val validSubmitSub = form.onValidSubmit().test()
        val submitFailedSub = form.onSubmitFailed().test()

        emailObservable.onNext("")
        passwordObservable.onNext("")
        ageObservable.onNext(MIN_AGE_VALUE - 1)

        fieldsSub.assertNoValues()
                .assertNoErrors()

        formSub.assertNoValues()
                .assertNoErrors()

        validSubmitSub.assertNoValues()
                .assertNoErrors()

        submitFailedSub.assertNoValues()
                .assertNoErrors()

        emailObservable.onNext(VALID_EMAIL)
        passwordObservable.onNext(VALID_PASSWORD)
        ageObservable.onNext(MIN_AGE_VALUE)

        fieldsSub.assertNoValues()
                .assertNoErrors()

        formSub.assertNoValues()
                .assertNoErrors()

        validSubmitSub.assertNoValues()
                .assertNoErrors()

        submitFailedSub.assertNoValues()
                .assertNoErrors()

    }

    @Test
    fun shouldBuildFormWithoutFieldValidations() {
        val form = getBuilder<Int>(submit).build()

        val fieldsSub = form.onFieldValidationChange().test()
        val formSub = form.onFormValidationChange().test()
        val validSubmitSub = form.onValidSubmit().test()
        val submitFailedSub = form.onSubmitFailed().test()

        submit.onNext(Unit)

        fieldsSub.assertNoValues()
                .assertNoErrors()

        formSub.assertValues(true)
                .assertNoErrors()

        validSubmitSub.assertValue(emptyList())
                .assertNoErrors()

        submitFailedSub.assertNoValues()
                .assertNoErrors()
    }
}
