package br.com.youse.forms.form.changes

import br.com.youse.forms.form.FormState
import br.com.youse.forms.form.IObservableChange.ChangeObserver
import br.com.youse.forms.form.isTrue

open class FormAllowsValidationOnChange(private val formState: FormState,
                                        private val validate: () -> Unit) : ChangeObserver {
    override fun onChange() {
        val isFormValidationEnabled = formState.isFormValidationEnabled.value.isTrue()
        if (isFormValidationEnabled) {
            validate()
        }
    }
}