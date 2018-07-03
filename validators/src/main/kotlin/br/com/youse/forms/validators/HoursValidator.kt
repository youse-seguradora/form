package br.com.youse.forms.validators

import br.com.youse.forms.validators.ValidationTypes.Companion.HOUR_FORMAT

class HoursValidator(val message: String, val divider: String) : Validator<String> {
    override fun validationMessage(): ValidationMessage {
        return ValidationMessage(message = message, validationType = HOUR_FORMAT)
    }

    override fun isValid(input: String): Boolean {
        return try {
            val parts = input.split(divider)
            val hours = parts.firstOrNull()?.toInt() ?: -1
            val minutes = parts.lastOrNull()?.toInt() ?: -1
            (parts.size == 2
                    && hours >= 0
                    && hours <= 23
                    && minutes >= 0
                    && minutes <= 59)
        } catch (e: Throwable) {
            false
        }
    }
}