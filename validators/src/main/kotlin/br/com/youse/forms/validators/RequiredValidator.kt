package br.com.youse.forms.validators

import br.com.youse.forms.validators.ValidationTypes.Companion.REQUIRED

class RequiredValidator(val message: String) : Validator<CharSequence> {

    private val validationMessage = ValidationMessage(message = message, validationType = REQUIRED)

    override fun validationMessage(): ValidationMessage {
        return validationMessage
    }

    override fun isValid(input: CharSequence): Boolean {
        return input.isNotEmpty()
    }
}