package br.com.youse.forms.form.changes

import br.com.youse.forms.form.FormState
import br.com.youse.forms.form.IObservableChange.ChangeObserver
import br.com.youse.forms.form.isTrue

open class FieldAllowsValidationOnChange(private val formState: FormState,
                                         private val validate: () -> Unit) : ChangeObserver {
    override fun onChange() {
        val isFieldValidationEnabled = formState.isFieldValidationEnabled.value.isTrue()
        if (isFieldValidationEnabled) {
            validate()
        }
    }
}