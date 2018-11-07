package br.com.youse.forms.form.changes

import br.com.youse.forms.form.FormState
import br.com.youse.forms.form.IObservableChange.ChangeObserver
import br.com.youse.forms.form.isTrue
import br.com.youse.forms.form.models.FormField

internal class InputChangeObserver<T>(private val formState: FormState,
                                      private val field: FormField<T, *>) : ChangeObserver {

    private val strategy = formState.strategy

    override fun onChange() {

        if (shouldValidateField()) {
            field.validate()
        }

        if (shouldClearErrorsOnChange()) {
            field.cleanErrors()
        }
    }

    private fun shouldValidateField(): Boolean {
        val fieldAllowsValidation = field.enabled.value.isTrue()
        val formAllowsFieldValidation = formState.isFieldValidationEnabled.value.isTrue()
        val strategyAllowsValidation = strategy.onChange

        return fieldAllowsValidation
                && formAllowsFieldValidation
                && strategyAllowsValidation
    }

    private fun shouldClearErrorsOnChange(): Boolean {
        val fieldAllowsValidation = field.enabled.value.isTrue()
        val formAllowsFieldValidation = formState.isFieldValidationEnabled.value.isTrue()
        val strategyAllowsClearErrorOnChange = strategy.clearErrorOnChange
        val hasErrors = field.hasErrors()

        return fieldAllowsValidation
                && formAllowsFieldValidation
                && strategyAllowsClearErrorOnChange
                && hasErrors
    }
}
