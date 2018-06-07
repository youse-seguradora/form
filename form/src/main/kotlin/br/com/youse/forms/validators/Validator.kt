package br.com.youse.forms.validators

interface Validator<in T> {
    fun isValid(input: T): Boolean
    fun validationMessage(): ValidationMessage
}

interface ValidationType

data class ValidationMessage(val message: String, val validationType: ValidationType)