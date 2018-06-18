package br.com.youse.forms.validators

/**
 * Interface to validate an input, each Validator should validate only one aspect of an input value
 * and return a proper validation message.
 */
interface Validator<in T> {
    /**
     *  Validates if the input is a valid value.
     *  Returns true if the input is valid, false otherwise.
     */
    fun isValid(input: T): Boolean

    /**
     * Returns a validation message, it is used only when isValid method returns false.
     */
    fun validationMessage(): ValidationMessage
}

/**
 * A ValidationType represented why validation failed.
 * Multiple Validators can have the same ValidationType.
 */
interface ValidationType

/**
 * Holds a validation failed message and the validation type.
 */
data class ValidationMessage(val message: String, val validationType: ValidationType)