package br.com.youse.forms.form

import br.com.youse.forms.validators.ValidationMessage

interface FieldValidationChange {
    /**
     * It's called every time a field validation changes.
     * {@code validation} contains a list of validation messages,
     * if the validation messages list is empty the field it valid.
     */
    fun onFieldValidationChange(validations: List<ValidationMessage>)
}