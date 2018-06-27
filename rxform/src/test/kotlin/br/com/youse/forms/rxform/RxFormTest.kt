package br.com.youse.forms.rxform

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.ValidationType
import br.com.youse.forms.validators.Validator
import io.reactivex.subjects.PublishSubject

import org.junit.Test

class RxFormTest {
    companion object {
        const val EMAIL_ID = 1
        const val PASSWORD_ID = 2
        const val AGE_ID = 3
        const val VALID_EMAIL = "some_email_@some_domain.com"
        const val VALID_PASSWORD = "12345678"
        const val MIN_AGE_VALUE = 21
        const val MAX_AGE_VALUE = 100

        private const val VALID_EMAIL_TYPE: ValidationType = "VALID_EMAIL_TYPE"
        private const val VALID_PASSWORD_TYPE: ValidationType = "VALID_PASSWORD_TYPE"
        private const val MIN_VALUE_TYPE: ValidationType = "MIN_VALUE_TYPE"
        private const val MAX_VALUE_TYPE: ValidationType = "MAX_VALUE_TYPE"

        val INVALID_EMAIL_MESSAGE = ValidationMessage("input is not VALID_EMAIL", VALID_EMAIL_TYPE)
        val INVALID_PASSWORD_MESSAGE = ValidationMessage("input is not VALID_PASSWORD", VALID_PASSWORD_TYPE)
        val TOO_LARGE_MESSAGE = ValidationMessage("input is too large", MAX_VALUE_TYPE)
        val TOO_SMALL_MESSAGE = ValidationMessage("input is too small", MIN_VALUE_TYPE)
    }


    private val submit = PublishSubject.create<Unit>()
    private val emailObservable = PublishSubject.create<String>()
    private val passwordObservable = PublishSubject.create<CharSequence>()
    private val ageObservable = PublishSubject.create<Int>()

    private val emailValidators = listOf(object : Validator<String> {
        override fun isValid(input: String): Boolean {
            return VALID_EMAIL == input
        }

        override fun validationMessage(): ValidationMessage {
            return INVALID_EMAIL_MESSAGE
        }
    })

    private val passwordValidators = listOf(object : Validator<CharSequence> {
        override fun isValid(input: CharSequence): Boolean {
            return VALID_PASSWORD == input
        }

        override fun validationMessage(): ValidationMessage {
            return INVALID_PASSWORD_MESSAGE
        }
    })

    private val ageValidators = listOf(object : Validator<Int> {

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
        val form = RxForm.Builder<Int>(submit, ValidationStrategy.ALL_TIME)
                .addFieldValidations(EMAIL_ID, emailObservable, emailValidators)
                .addFieldValidations(PASSWORD_ID, passwordObservable, passwordValidators)
                .addFieldValidations(AGE_ID, ageObservable, ageValidators)
                .build()

        val fieldsSub = form.onFieldValidationChange().test()
        val formSub = form.onFormValidationChange().test()
        val validSubmitSub = form.onValidSubmit().test()
        val firstValidationFailedSub = form.onSubmitValidationFailed().test()

        emailObservable.onNext("foo")
        passwordObservable.onNext("bar")
        ageObservable.onNext(MIN_AGE_VALUE - 1)


        fieldsSub.assertValues(
                Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
                Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE))
        )

        formSub.assertValue(false)

        firstValidationFailedSub.assertValue(listOf(
                Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE))
        ))

        validSubmitSub.assertNoValues().assertNoErrors()

    }

    @Test
    fun shouldExecuteValidationAfterSubmit() {
        val form = RxForm.Builder<Int>(submit)
                .addFieldValidations(EMAIL_ID, emailObservable, emailValidators)
                .addFieldValidations(PASSWORD_ID, passwordObservable, passwordValidators)
                .addFieldValidations(AGE_ID, ageObservable, ageValidators)
                .build()

        val fieldsSub = form.onFieldValidationChange().test()
        val formSub = form.onFormValidationChange().test()
        val validSubmitSub = form.onValidSubmit().test()
        val firstValidationFailedSub = form.onSubmitValidationFailed().test()

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

        firstValidationFailedSub.assertValue(listOf(
                Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE))
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

        firstValidationFailedSub.assertValues(listOf(
                Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE))
        ))

        validSubmitSub.assertValue(listOf(
                Pair(EMAIL_ID, VALID_EMAIL),
                Pair(PASSWORD_ID, VALID_PASSWORD),
                Pair(AGE_ID, MIN_AGE_VALUE)
        ))

    }

    @Test
    fun shouldNotValidateBeforeSubmit() {
        val form = RxForm.Builder<Int>(submit)
                .addFieldValidations(EMAIL_ID, emailObservable, emailValidators)
                .addFieldValidations(PASSWORD_ID, passwordObservable, passwordValidators)
                .addFieldValidations(AGE_ID, ageObservable, ageValidators)
                .build()

        val fieldsSub = form.onFieldValidationChange().test()
        val formSub = form.onFormValidationChange().test()
        val validSubmitSub = form.onValidSubmit().test()
        val firstValidationFailedSub = form.onSubmitValidationFailed().test()

        emailObservable.onNext("")
        passwordObservable.onNext("")
        ageObservable.onNext(MIN_AGE_VALUE - 1)

        fieldsSub.assertNoValues()
                .assertNoErrors()

        formSub.assertNoValues()
                .assertNoErrors()

        validSubmitSub.assertNoValues()
                .assertNoErrors()

        firstValidationFailedSub.assertNoValues()
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

        firstValidationFailedSub.assertNoValues()
                .assertNoErrors()

    }

    @Test
    fun shouldBuildFormWithoutFieldValidations() {
        val form = RxForm.Builder<Int>(submit).build()

        val fieldsSub = form.onFieldValidationChange().test()
        val formSub = form.onFormValidationChange().test()
        val validSubmitSub = form.onValidSubmit().test()
        val firstValidationFailedSub = form.onSubmitValidationFailed().test()

        submit.onNext(Unit)

        fieldsSub.assertNoValues()
                .assertNoErrors()

        formSub.assertValues(true)
                .assertNoErrors()

        validSubmitSub.assertNoValues()
                .assertNoErrors()

        firstValidationFailedSub.assertNoValues()
                .assertNoErrors()
    }
}