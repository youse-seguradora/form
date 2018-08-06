package br.com.youse.forms.livedata

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.ValidationType
import br.com.youse.forms.validators.Validator
import org.junit.Rule
import org.junit.rules.TestRule
import kotlin.test.*

class LiveDataFormTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

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

    private var emailLiveData = MutableLiveData<String>()
    private var passwordLiveData = MutableLiveData<String>()
    private var ageLiveData = MutableLiveData<Int>()

    @BeforeTest
    fun setup() {
        emailLiveData = MutableLiveData()
        passwordLiveData = MutableLiveData()
        ageLiveData = MutableLiveData()


    }

    private fun <T> setupCountableOnChange(ld: MutableLiveData<T>, count: Int, callback: (T?) -> Unit): Observer<T> {
        val obs = object : Observer<T> {
            var times = 0
            override fun onChanged(t: T?) {
                callback(t)
                times += 1
                if (times >= count) {
                    ld.removeObserver(this)
                }
            }
        }
        ld.observeForever(obs)
        return obs
    }

    @Test
    fun shouldValidateAfterSubmit() {
        validate(true)
    }

    @Test
    fun shouldValidateAllTime() {
        validate(false)
    }


    private fun validate(afterSubmit: Boolean) {
        val strategy = if (afterSubmit) ValidationStrategy.AFTER_SUBMIT else ValidationStrategy.ALL_TIME

        val form: LiveDataForm<Int> = LiveDataForm.Builder<Int>(strategy = strategy)
                .addFieldValidations(EMAIL_ID, emailLiveData, emailValidators)
                .addFieldValidations(PASSWORD_ID, passwordLiveData, passwordValidators)
                .addFieldValidations(AGE_ID, ageLiveData, ageValidators)
                .build()

        var emailMessages: List<ValidationMessage>? = null
        val emailValidation = form.onFieldValidationChange[EMAIL_ID]!!
        setupCountableOnChange(emailValidation, 2) {
            emailMessages = it
        }

        var passwordMessages: List<ValidationMessage>? = null
        val passwordValidation = form.onFieldValidationChange[PASSWORD_ID]!!
        setupCountableOnChange(passwordValidation, 2) {
            passwordMessages = it
        }

        var ageMessages: List<ValidationMessage>? = null
        val ageValidation = form.onFieldValidationChange[AGE_ID]!!
        setupCountableOnChange(ageValidation, 2) {
            ageMessages = it
        }

        val submit = form.submit
        setupCountableOnChange(submit, 2) {
            // nothing
        }

        val failedSubmit = form.onSubmitFailed
        var submitFailedMessages: List<Pair<Int, List<ValidationMessage>>>? = null
        setupCountableOnChange(failedSubmit, 1) {
            submitFailedMessages = it
        }

        val formValidation = form.onFormValidationChange
        var currentFormValidation: Boolean? = null
        setupCountableOnChange(formValidation, 2) {
            currentFormValidation = it
        }
        val validSubmit = form.onValidSubmit
        var validSubmitContents: List<Pair<Int, Any?>>? = null
        setupCountableOnChange(validSubmit, 1) {
            validSubmitContents = it
        }

        assertNull(emailMessages)
        assertNull(passwordMessages)
        assertNull(ageMessages)
        assertNull(submitFailedMessages)
        assertNull(currentFormValidation)
        assertNull(validSubmitContents)

        emailLiveData.value = ""
        passwordLiveData.value = ""
        ageLiveData.value = MIN_AGE_VALUE - 1

        if (afterSubmit) {
            // nothing should be initialized yet
            assertNull(emailMessages)
            assertNull(passwordMessages)
            assertNull(ageMessages)
            assertNull(submitFailedMessages)
            assertNull(currentFormValidation)
            assertNull(validSubmitContents)
        } else {
            // validate fields all the time
            assertNotNull(emailMessages)
            assertNotNull(passwordMessages)
            assertNotNull(ageMessages)

            // form state is invalid
            assertNotNull(currentFormValidation)
            assertFalse(currentFormValidation!!)

            // did not call the submit related callbacks
            assertNull(submitFailedMessages)
            assertNull(validSubmitContents)
        }

        submit.value = true

        // field validation was triggered
        assertNotNull(emailMessages)
        assertNotNull(passwordMessages)
        assertNotNull(ageMessages)

        // form state validation was triggered
        assertNotNull(currentFormValidation)

        // the submit caused an evalution and the form submission failed
        assertNotNull(submitFailedMessages)
        assertNull(validSubmitContents)

        // checking fields validation messages
        assertEquals(emailMessages!!.first(), INVALID_EMAIL_MESSAGE)
        assertEquals(passwordMessages!!.first(), INVALID_PASSWORD_MESSAGE)
        assertEquals(ageMessages!!.first(), TOO_SMALL_MESSAGE)

        // the form is not valid
        assertFalse(currentFormValidation!!)

        // the failed submit messages
        assertEquals(submitFailedMessages, listOf(
                Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
                Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE)))
        )

        // still not triggered
        assertNull(validSubmitContents)


        // valid form
        emailLiveData.value = VALID_EMAIL
        passwordLiveData.value = VALID_PASSWORD
        ageLiveData.value = MIN_AGE_VALUE

        // all messages are empty
        assertTrue(emailMessages!!.isEmpty())
        assertTrue(passwordMessages!!.isEmpty())
        assertTrue(ageMessages!!.isEmpty())

        // the form is valid
        assertTrue(currentFormValidation!!)

        submit.value = true

        // valid form submit triggered
        assertEquals(validSubmitContents, listOf(
                Pair(EMAIL_ID, VALID_EMAIL),
                Pair(PASSWORD_ID, VALID_PASSWORD),
                Pair(AGE_ID, MIN_AGE_VALUE))
        )

    }

}