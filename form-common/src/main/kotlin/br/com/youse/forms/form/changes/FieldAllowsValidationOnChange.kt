package br.com.youse.forms.form.changes

import br.com.youse.forms.form.FormState
import br.com.youse.forms.form.IObservableChange.ChangeObserver
import br.com.youse.forms.form.isTrue
import br.com.youse.forms.validators.ValidationStrategy

open class FieldAllowsValidationOnChange(
        private val strategy: ValidationStrategy,
        private val formState: FormState,
        private val validate: () -> Unit) : ChangeObserver {

    override fun onChange() {
        val isFieldValidationEnabled = formState.submitStateAllowsFieldValidation(strategy)
        if (isFieldValidationEnabled) {
            validate()
        }
    }
}