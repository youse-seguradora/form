package br.com.youse.forms.form

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.Validator

interface IForm<T> {
    interface ValidSubmit<T> {
        fun onValidSubmit(fields: List<Pair<T, Any>>)
    }

    interface SubmitValidationFailed<T> {
        fun onValidationFailed(validations: List<Pair<T, List<ValidationMessage>>>)
    }

    interface FormValidationChange {
        fun onChange(isValid: Boolean)
    }

    interface FieldValidationChange<T> {
        fun onChange(messages: Pair<T, List<ValidationMessage>>)
    }

    class ObservableValue<T>(private val _value: T) {

        interface ValueObserver<T> {
            fun onChange(value: T)
        }

        private var listener: IForm.ObservableValue.ValueObserver<T>? = null

        var value: T = _value
            set(newValue) {
                listener?.onChange(newValue)
            }

        fun setValueListener(valueObserver: IForm.ObservableValue.ValueObserver<T>) {
            listener = valueObserver
            value = _value
        }
    }

    fun doSubmit()

    interface Builder<T> {
        fun setFieldValidationListener(listener: IForm.FieldValidationChange<T>): Builder<T>
        fun setFormValidationListener(listener: IForm.FormValidationChange): Builder<T>
        fun setValidSubmitListener(listener: IForm.ValidSubmit<T>): Builder<T>
        fun setSubmitValidationFailedListener(listener: IForm.SubmitValidationFailed<T>): Builder<T>
        fun <R> addFieldValidations(key: T, observableValue: IForm.ObservableValue<R>, validators: List<Validator<R>>): IForm.Builder<T>
        fun build(): IForm<T>
    }
}