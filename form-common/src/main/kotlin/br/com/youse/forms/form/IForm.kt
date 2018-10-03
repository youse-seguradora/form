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

import br.com.youse.forms.form.models.FormField
import br.com.youse.forms.validators.ValidationMessage


interface IForm {

    interface ValidSubmit<T> {
        /**
         * It's called when a submit happens and the form is valid.
         * {@code fields} is list of {@link Pair}, each one with the field key and the current value of that field.
         * NOTE: As each field can be of a different type we need to use Any here.
         */
        fun onValidSubmit(fields: List<Pair<T, Any?>>)
    }

    interface SubmitFailed<T> {
        /**
         * It's called when a submit happens and the form is not valid.
         * {@code validations} is an sorted list of {@link Pair}, each one with the field key and a list of validation messages.
         * This is useful in case you want to scroll or give focus to the first or last invalid field.
         */
        fun onSubmitFailed(validations: List<Pair<T, List<ValidationMessage>>>)
    }

    interface FormValidationChange {
        /**
         * It's called every time the form validation changes.
         * {@code isValid} is boolean indicating if the form is valid (true) or not (false).
         */
        fun onFormValidationChange(isValid: Boolean)
    }

    interface FieldValidationChange<T> {
        /**
         * It's called every time a field validation changes.
         * {@code validation} contains the field key and a list of validation messages,
         * if the validation messages list is empty the field it valid.
         */
        fun onFieldValidationChange(key: T, validations: List<ValidationMessage>)
    }

    /**
     * Used to indicate that the form should be submitted.
     */
    fun doSubmit()

    interface Builder<T> {

        /**
         * Sets a field validation listener.
         */
        fun setFieldValidationListener(listener: FieldValidationChange<T>): Builder<T>

        /**
         * Sets a form validation listener.
         */
        fun setFormValidationListener(listener: FormValidationChange): Builder<T>

        /**
         * Sets a valid submit listener.
         */
        fun setValidSubmitListener(listener: ValidSubmit<T>): Builder<T>

        /**
         * Sets a failed submit listener
         */
        fun setSubmitFailedListener(listener: SubmitFailed<T>): Builder<T>

        /**
         * Adds a field to the builder, it takes a {@code field} of FormField type.
         */
        fun <R> addField(field: FormField<T, R>): IForm.Builder<T>

        /**
         * Builds the {@code IForm}.
         */
        fun build(): IForm
    }
}