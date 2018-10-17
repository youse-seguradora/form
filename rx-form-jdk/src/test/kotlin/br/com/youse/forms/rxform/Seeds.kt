package br.com.youse.forms.rxform

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationType
import br.com.youse.forms.validators.Validator


const val EMAIL_ID = 1
const val PASSWORD_ID = 2
const val AGE_ID = 3
const val PASSWORD_CONFIRMATION_ID = 4
const val VALID_EMAIL = "some_email_@some_domain.com"
const val VALID_PASSWORD = "12345678"
const val MIN_AGE_VALUE = 21
const val MAX_AGE_VALUE = 100

val VALID_EMAIL_TYPE: ValidationType = object : ValidationType {}
val VALID_PASSWORD_TYPE: ValidationType = object : ValidationType {}
val MIN_VALUE_TYPE: ValidationType = object : ValidationType {}
val MAX_VALUE_TYPE: ValidationType = object : ValidationType {}
val PASSWORD_CONFIRMATION_MISMATCH: ValidationType = object : ValidationType {}

val INVALID_EMAIL_MESSAGE = ValidationMessage("Input is not VALID_EMAIL", VALID_EMAIL_TYPE)
val INVALID_PASSWORD_MESSAGE = ValidationMessage("Input is not VALID_PASSWORD", VALID_PASSWORD_TYPE)
val TOO_LARGE_MESSAGE = ValidationMessage("Input is too large", MAX_VALUE_TYPE)
val TOO_SMALL_MESSAGE = ValidationMessage("Input is too small", MIN_VALUE_TYPE)

val PASSWORD_CONFIRMATION_MISMATCH_MESSAGE = ValidationMessage("Password is not equals password confirmation", PASSWORD_CONFIRMATION_MISMATCH)

val emailValidators: List<Validator<String>> = listOf(object : Validator<String> {
    override fun isValid(input: String?): Boolean {
        return input != null && VALID_EMAIL == input
    }

    override fun validationMessage(): ValidationMessage {
        return INVALID_EMAIL_MESSAGE
    }
})

val passwordValidators: List<Validator<String>> = listOf(object : Validator<String> {
    override fun isValid(input: String?): Boolean {
        return input != null && VALID_PASSWORD == input
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
val validSubmitWithMinValue = listOf(
        Pair(EMAIL_ID, VALID_EMAIL),
        Pair(PASSWORD_ID, VALID_PASSWORD),
        Pair(PASSWORD_CONFIRMATION_ID, VALID_PASSWORD),
        Pair(AGE_ID, MIN_AGE_VALUE)
)

val validationsWithMinValue = listOf(
        Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
        Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
        Pair(PASSWORD_CONFIRMATION_ID, listOf(PASSWORD_CONFIRMATION_MISMATCH_MESSAGE)),
        Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE))
)

val validationsEmptyValidations = listOf(
        Pair(EMAIL_ID, emptyList()),
        Pair(PASSWORD_ID, emptyList()),
        Pair(PASSWORD_CONFIRMATION_ID, emptyList()),
        Pair(AGE_ID, emptyList<ValidationMessage>())
)

val validationsWithMaxValue = listOf(
        Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
        Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
        Pair(PASSWORD_CONFIRMATION_ID, listOf(PASSWORD_CONFIRMATION_MISMATCH_MESSAGE)),
        Pair(AGE_ID, listOf(TOO_LARGE_MESSAGE))
)

val validationsWithMinAndMaxValue = listOf(
        Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
        Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
        Pair(PASSWORD_CONFIRMATION_ID, listOf(PASSWORD_CONFIRMATION_MISMATCH_MESSAGE)),
        Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE)),
        Pair(AGE_ID, listOf(TOO_LARGE_MESSAGE))
)


