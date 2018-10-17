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

import br.com.youse.forms.rxform.models.RxField
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.junit.After
import org.junit.Before
import kotlin.test.Test


class RxFormTests {


    private lateinit var form: IRxForm<Int>

    lateinit var emailField: RxField<Int, String>

    lateinit var passwordField: RxField<Int, String>

    lateinit var passwordConfirmationField: RxField<Int, String>

    lateinit var ageField: RxField<Int, Int>

    private lateinit var fieldsSub: TestObserver<Pair<Int, List<ValidationMessage>>>

    private lateinit var formSub: TestObserver<Boolean>

    private lateinit var validSubmitSub: TestObserver<List<Pair<Int, Any?>>>

    private lateinit var submitFailedSub: TestObserver<List<Pair<Int, List<ValidationMessage>>>>

    private val passwordConfirmationValidators: List<Validator<String>> = listOf(object : Validator<String> {
        override fun isValid(input: String?): Boolean {
            return input != null && passwordObservable.value == input
        }

        override fun validationMessage(): ValidationMessage {
            return PASSWORD_CONFIRMATION_MISMATCH_MESSAGE
        }
    })

    private fun buildRegistrationForm(strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT) {

        emailField = RxField(
                EMAIL_ID,
                emailObservable,
                validators = emailValidators
        )

        passwordField = RxField(
                PASSWORD_ID,
                passwordObservable,
                validators = passwordValidators
        )

        passwordConfirmationField = RxField(
                PASSWORD_CONFIRMATION_ID,
                passwordConfirmationObservable,
                validators = passwordConfirmationValidators,
                validationTriggers = listOf(passwordObservable.map { Unit })
        )

        ageField = RxField(
                AGE_ID,
                ageObservable,
                validators = ageValidators
        )

        form = RxForm.Builder<Int>(submit, strategy)
                .addField(emailField)
                .addField(passwordField)
                .addField(passwordConfirmationField)
                .addField(ageField)
                .build()

        fieldsSub = form.onFieldValidationChange().test()
        formSub = form.onFormValidationChange().test()
        validSubmitSub = form.onValidSubmit().test()
        submitFailedSub = form.onSubmitFailed().test()
    }

    private fun buildEmptyForm(strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT) {
        form = RxForm.Builder<Int>(submit, strategy).build()

        fieldsSub = form.onFieldValidationChange().test()
        formSub = form.onFormValidationChange().test()
        validSubmitSub = form.onValidSubmit().test()
        submitFailedSub = form.onSubmitFailed().test()
    }


    private lateinit var submit: PublishSubject<Unit>
    private lateinit var emailObservable: BehaviorSubject<String>
    private lateinit var passwordObservable: BehaviorSubject<String>
    private lateinit var passwordConfirmationObservable: BehaviorSubject<String>
    private lateinit var ageObservable: BehaviorSubject<Int>


    @Before
    fun setup() {
        submit = PublishSubject.create<Unit>()
        emailObservable = BehaviorSubject.create<String>()
        passwordObservable = BehaviorSubject.create<String>()
        passwordConfirmationObservable = BehaviorSubject.create<String>()
        ageObservable = BehaviorSubject.create<Int>()

    }

    @After
    fun tearDown() {
        form.dispose()
        fieldsSub.dispose()
        formSub.dispose()
        validSubmitSub.dispose()
        submitFailedSub.dispose()
    }

    @Test
    fun shouldValidateAllTheTime() {

        emailObservable.onNext("foo")
        passwordObservable.onNext("bar")
        passwordConfirmationObservable.onNext("bar1")
        ageObservable.onNext(MIN_AGE_VALUE - 1)

        buildRegistrationForm(ValidationStrategy.ALL_TIME)

        validationsWithMinValue.forEachIndexed { index, validation ->
            fieldsSub.assertValueAt(index, validation)
        }

        formSub.assertValue(false)

        validSubmitSub.assertNoValues().assertNoErrors()

        submitFailedSub.assertNoValues().assertNoErrors()

    }

    @Test
    fun shouldValidateConfirmPasswordFieldWhenPasswordChange() {

        emailObservable.onNext(VALID_EMAIL)
        passwordObservable.onNext(VALID_PASSWORD)
        passwordConfirmationObservable.onNext("bar")
        ageObservable.onNext(MIN_AGE_VALUE)

        buildRegistrationForm(ValidationStrategy.ALL_TIME)

        fieldsSub.assertValueAt(2,
                Pair(PASSWORD_CONFIRMATION_ID, listOf(PASSWORD_CONFIRMATION_MISMATCH_MESSAGE)))

        formSub.assertValue(false)

        passwordConfirmationObservable.onNext(VALID_PASSWORD)

        fieldsSub.assertValueAt(4,
                Pair(PASSWORD_CONFIRMATION_ID, emptyList()))


        formSub.assertValues(false, true)

        passwordObservable.onNext("bar")

        passwordConfirmationField.errors.firstOrError()
                .test()
                .assertValue(listOf(PASSWORD_CONFIRMATION_MISMATCH_MESSAGE))
                .dispose()

        fieldsSub.assertValueAt(5,
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)))

        fieldsSub.assertValueAt(6,
                Pair(PASSWORD_CONFIRMATION_ID, listOf(PASSWORD_CONFIRMATION_MISMATCH_MESSAGE)))

        formSub.assertValues(false, true, false)

        validSubmitSub.assertNoValues().assertNoErrors()

        submitFailedSub.assertNoValues().assertNoErrors()

    }

    @Test
    fun shouldExecuteValidationAfterSubmit() {

        buildRegistrationForm()

        emailObservable.onNext("foo")
        passwordObservable.onNext("bar")
        passwordConfirmationObservable.onNext("bar1")
        ageObservable.onNext(MIN_AGE_VALUE - 1)

        submit.onNext(Unit)

        validationsWithMinValue.forEachIndexed { index, validation ->
            fieldsSub.assertValueAt(index, validation)
        }

        formSub.assertValue(false)

        submitFailedSub.assertValue(validationsWithMinValue)

        validSubmitSub.assertNoValues().assertNoErrors()

        emailObservable.onNext(VALID_EMAIL)
        passwordObservable.onNext(VALID_PASSWORD)
        passwordConfirmationObservable.onNext(VALID_PASSWORD)
        ageObservable.onNext(MIN_AGE_VALUE)

        (validationsWithMinValue + validationsEmptyValidations)
                .forEachIndexed { index, validation ->
                    fieldsSub.assertValueAt(index, validation)
                }

        formSub.assertValues(false, true)

        submit.onNext(Unit)

        submitFailedSub.assertValue(validationsWithMinValue)

        validSubmitSub.assertValue(validSubmitWithMinValue)

    }

    @Test
    fun shouldNotValidateBeforeSubmit() {

        buildRegistrationForm()

        emailObservable.onNext("")
        passwordObservable.onNext("")
        passwordConfirmationObservable.onNext("")
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
        passwordConfirmationObservable.onNext(VALID_PASSWORD)
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

        buildEmptyForm()

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
