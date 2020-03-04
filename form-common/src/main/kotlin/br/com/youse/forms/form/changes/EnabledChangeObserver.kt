package br.com.youse.forms.form.changes

import br.com.youse.forms.form.FormState
import br.com.youse.forms.form.IObservableChange.ChangeObserver
import br.com.youse.forms.form.isTrue
import br.com.youse.forms.form.models.FormField
import br.com.youse.forms.validators.ValidationStrategy

internal class EnabledChangeObserver<T>(
        formStrategy: ValidationStrategy,
        private val formState: FormState,
        private val field: FormField<T, *>,
        private val validateForm: () -> Unit) : ChangeObserver {

    private val strategy = field.strategy ?: formStrategy

    override fun onChange() {
        val validateFieldOnEnable = shouldValidateFieldOnEnable()

        if (validateFieldOnEnable) {
            field.validate()
        }

        val clearErrorsOnDisable = shouldClearErrorsOnDisable()

        if (clearErrorsOnDisable) {
            field.cleanErrors()
        }

        val formAllowsFieldValidation = formState.submitStateAllowsFieldValidation(strategy)

        val requestFormStateUpdate = !validateFieldOnEnable
                && !clearErrorsOnDisable
                && formAllowsFieldValidation

        if (requestFormStateUpdate) {
            validateForm()
        }
    }

    private fun shouldValidateFieldOnEnable(): Boolean {
        val fieldAllowsValidation = field.enabled.value.isTrue()
        val formAllowsFieldValidation = formState.submitStateAllowsFieldValidation(strategy)
        val strategyAllowsValidationOnEnable = strategy.onEnable

        return fieldAllowsValidation
                && formAllowsFieldValidation
                && strategyAllowsValidationOnEnable
    }

    private fun shouldClearErrorsOnDisable(): Boolean {
        val fieldIsDisabled = !field.enabled.value.isTrue()
        val strategyAllowsClearErrorsOnDisable = strategy.clearErrorsOnDisable
        val hasErrors = field.hasErrors()
        return fieldIsDisabled
                && strategyAllowsClearErrorsOnDisable
                && hasErrors
    }

}