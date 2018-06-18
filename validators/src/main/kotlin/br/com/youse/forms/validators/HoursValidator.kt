package br.com.youse.forms.validators

class HoursValidator(val message: String, val splitter: String) : Validator<String> {
    override fun validationMessage(): ValidationMessage {
        return ValidationMessage(message = message, validationType = HOUR_FORMAT)
    }

    override fun isValid(input: String): Boolean {
        val parts = input.split(splitter)
        val hours = parts.firstOrNull()?.toInt() ?: -1
        val minutes = parts.lastOrNull()?.toInt() ?: -1
        return parts.size == 2
                && hours >= 0
                && hours <= 23
                && minutes >= 0
                && minutes <= 59
    }
}