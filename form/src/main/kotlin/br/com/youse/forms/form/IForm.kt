/**
MIT License

Copyright (c) 2018 Youse Seguros

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package br.com.youse.forms.form

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.Validator
import kotlin.properties.Delegates


interface IForm<T> {

    interface ValidSubmit<T> {
        /**
         * It's called when a submit happens and the form is valid.
         * {@code fields} is list of {@link Pair}, each one with the field key and the current value of that field.
         * NOTE: As each field can be of a different type we need to use Any here.
         */
        fun onValidSubmit(fields: List<Pair<T, Any>>)
    }

    interface SubmitFailed<T> {
        /**
         * It's called when a submit happens and the form is not valid.
         * {@code validations} is an sorted list of {@link Pair}, each one with the field key and a list of validation messages.
         * This is useful in case you want to scroll or give focus to the first or last invalid field.
         */
        fun onValidationFailed(validations: List<Pair<T, List<ValidationMessage>>>)
    }

    interface FormValidationChange {
        /**
         * It's called every time the form validation changes.
         * {@code isValid} is boolean indicating if the form is valid (true) or not (false).
         */
        fun onChange(isValid: Boolean)
    }

    interface FieldValidationChange<T> {
        /**
         * It's called every time a field validation changes.
         * {@code validation} contains the field key and a list of validation messages,
         * if the validation messages list is empty the field it valid.
         */
        fun onChange(validation: Pair<T, List<ValidationMessage>>)
    }

    /**
     * Class that notifies its listener every time the value changes.
     */
    class ObservableValue<T>(initialValue: T) {

        interface ValueObserver<T> {
            /**
             * Notifies a value change.
             */
            fun onChange(value: T)
        }

        private var listener: IForm.ObservableValue.ValueObserver<T>? = null

        var value: T by Delegates.observable(initialValue) { _, old, new ->
            if (old != new) {
                listener?.onChange(new)
            }
        }


        /**
         * Sets a listener for {@code value} changes.s
         */
        fun setValueListener(valueObserver: IForm.ObservableValue.ValueObserver<T>) {
            listener = valueObserver
            listener?.onChange(value)
        }
    }

    /**
     * Used to indicate that the form should be submitted.
     */
    fun doSubmit()

    interface Builder<T> {

        /**
         * Sets a field validation listener.
         */
        fun setFieldValidationListener(listener: IForm.FieldValidationChange<T>): Builder<T>

        /**
         * Sets a form validation listener.
         */
        fun setFormValidationListener(listener: IForm.FormValidationChange): Builder<T>

        /**
         * Sets a valid submit listener.
         */
        fun setValidSubmitListener(listener: IForm.ValidSubmit<T>): Builder<T>

        /**
         * Sets a failed submit listener
         */
        fun setSubmitFailedListener(listener: IForm.SubmitFailed<T>): Builder<T>

        /**
         * Adds a field to the builder, it takes a {@code key} to identify the field,
         * an {@code observableValue} that emits the field value changes and a list
         * of validators for that field.
         */
        fun <R> addFieldValidations(key: T, observableValue: IForm.ObservableValue<R>, validators: List<Validator<R>>): IForm.Builder<T>

        /**
         * Builds the {@code IForm}.
         */
        fun build(): IForm<T>
    }
}