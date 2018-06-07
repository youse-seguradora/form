package br.com.youse.forms.validators

class MinLengthValidator(val message: String, private val minLength: Int) : Validator<CharSequence> {

    private val validationMessage = ValidationMessage(message = message, validationType = MIN_LENGTH)

    override fun validationMessage(): ValidationMessage {
        return validationMessage
    }

    override fun isValid(input: CharSequence): Boolean {
        return input.length >= minLength
    }
}
