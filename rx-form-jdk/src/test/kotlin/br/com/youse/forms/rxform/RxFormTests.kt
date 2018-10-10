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

import br.com.youse.forms.validators.ValidationStrategy
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import kotlin.test.Test


class RxFormTests {
    private fun <T> getBuilder(submit: Observable<Unit>,
                               strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT): IRxForm.Builder<T> {
        return RxForm.Builder(submit, strategy)
    }


    private val submit = PublishSubject.create<Unit>()
    private val emailObservable = BehaviorSubject.create<String>()
    private val passwordObservable = BehaviorSubject.create<String>()
    private val ageObservable = BehaviorSubject.create<Int>()

    val emailField = RxField(EMAIL_ID, emailObservable, validators = emailValidators)
    val passwordField = RxField(PASSWORD_ID, passwordObservable, validators = passwordValidators)
    val ageField = RxField(AGE_ID, ageObservable, validators = ageValidators)


    @Before
    fun setup() {
        emailObservable.onNext("")
        passwordObservable.onNext("")
        ageObservable.onNext(0)
    }

    @Test
    fun shouldValidateAllTheTime() {

        val form = getBuilder<Int>(submit, ValidationStrategy.ALL_TIME)
                .addField(emailField)
                .addField(passwordField)
                .addField(ageField)
                .build()

        val fieldsSub = form.onFieldValidationChange().test()
        val formSub = form.onFormValidationChange().test()
        val validSubmitSub = form.onValidSubmit().test()
        val submitFailedSub = form.onSubmitFailed().test()

        emailObservable.onNext("foo")
        passwordObservable.onNext("bar")
        ageObservable.onNext(MIN_AGE_VALUE - 1)


        fieldsSub.assertValues(Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
                Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE)))

        formSub.assertValue(false)

        validSubmitSub.assertNoValues().assertNoErrors()
        submitFailedSub.assertNoValues().assertNoErrors()

        form.dispose()

    }

    @Test
    fun shouldExecuteValidationAfterSubmit() {

        val form = getBuilder<Int>(submit)
                .addField(emailField)
                .addField(passwordField)
                .addField(ageField)
                .build()

        val fieldsSub = form.onFieldValidationChange().test()
        val formSub = form.onFormValidationChange().test()
        val validSubmitSub = form.onValidSubmit().test()
        val submitFailedSub = form.onSubmitFailed().test()

        emailObservable.onNext("foo")
        passwordObservable.onNext("bar")
        ageObservable.onNext(MIN_AGE_VALUE - 1)

        submit.onNext(Unit)

        fieldsSub.assertValues(Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
                Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE)))

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

        form.dispose()
    }

    @Test
    fun shouldNotValidateBeforeSubmit() {
        val form = getBuilder<Int>(submit)
                .addField(emailField)
                .addField(passwordField)
                .addField(ageField)
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

        form.dispose()
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

        form.dispose()
    }
}
