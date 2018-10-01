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

import br.com.youse.forms.form.IForm.*
import br.com.youse.forms.form.IObservableChange.ChangeObserver
import br.com.youse.forms.form.models.FormField
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator

@Suppress("UNCHECKED_CAST")
class Form<T>(private val fieldValidationListener: FieldValidationChange<T>?,
              private val formValidationListener: FormValidationChange?,
              private val validSubmitListener: ValidSubmit<T>?,
              private val submitFailedListener: SubmitFailed<T>?,
              private val strategy: ValidationStrategy,
              private val fields: List<FormField<T, *>>) : IForm {


    private val lastFieldsMessages = mutableMapOf<T, List<ValidationMessage>>()

    private var isFormValid: Boolean? = null
    private var isFormSubmitted: Boolean? = null

    init {

        fields.forEach { field ->

            val key = field.key
            val validators = field.validators as List<Validator<Any?>>
            val validationTriggers = listOf<IObservableChange>(field.input) + field.validationTriggers

            val validationObserver = object : ChangeObserver {

                override fun onChange() {

                    val value = field.input.value

                    if (shouldValidate()) {
                        validateField(key, value, validators)
                        notifyFormValidationChangedIfChanged()
                    }
                }
            }

            validationTriggers.forEach { validationTrigger ->
                validationTrigger.addChangeListener(observer = validationObserver)
            }
        }
    }

    private fun shouldValidate(): Boolean {
        return (strategy == ValidationStrategy.ALL_TIME)
                ||
                (strategy == ValidationStrategy.AFTER_SUBMIT && isFormSubmitted == true)
    }

    private fun notifyFormValidationChangedIfChanged() {

        val areAllFieldsValid = areAllFieldValid()
        val hasFormValidationChanged = isFormValid != areAllFieldsValid

        if (hasFormValidationChanged) {
            // notify form validation changed
            formValidationListener?.onFormValidationChange(areAllFieldsValid)
        }

        isFormValid = areAllFieldsValid
    }

    private fun validateField(key: T, value: Any?, validators: List<Validator<Any?>>) {

        val messages = mutableListOf<ValidationMessage>()

        for (validator in validators) {
            if (!validator.isValid(value)) {
                messages += validator.validationMessage()
            }
        }

        val fieldLastMessages = lastFieldsMessages[key]
        val hasFieldValidationChanged = messages != fieldLastMessages

        if (hasFieldValidationChanged) {
            // notify field validation changed
            fieldValidationListener?.onFieldValidationChange(key, messages)
        }

        lastFieldsMessages[key] = messages
    }

    private fun areAllFieldValid(): Boolean {

        return lastFieldsMessages.isEmpty() || lastFieldsMessages.values
                .map { messages -> messages.isEmpty() }
                .reduce { acc, isValid -> acc && isValid }
    }

    private fun validateAllFields() {

        fields.forEach { field ->
            val key = field.key
            val validators = field.validators as List<Validator<Any?>>
            val value = field.input.value

            validateField(key, value, validators)
        }
    }

    private fun notifyValidSubmit() {

        val validFields = fields.map {
            Pair(it.key, it.input.value)
        }

        // notify a valid submit
        validSubmitListener?.onValidSubmit(validFields)
    }

    private fun notifySubmitFailed() {

        val validationMessages = lastFieldsMessages
                .filter { it.value.isNotEmpty() }
                .map { (key, messages) ->
                    Pair(key, messages)
                }

        // notify a not valid submit
        submitFailedListener?.onSubmitFailed(validationMessages)
    }

    override fun doSubmit() {

        isFormSubmitted = true

        if (strategy == ValidationStrategy.AFTER_SUBMIT) {
            validateAllFields()
            notifyFormValidationChangedIfChanged()
        }

        val areAllFieldsValid = areAllFieldValid()

        if (areAllFieldsValid) {
            notifyValidSubmit()
        } else {
            notifySubmitFailed()
        }
    }


    class Builder<T>(private val strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT) : IForm.Builder<T> {

        private var fieldValidationListener: FieldValidationChange<T>? = null
        private var formValidationListener: FormValidationChange? = null
        private var validSubmitListener: ValidSubmit<T>? = null
        private var submitFailedListener: SubmitFailed<T>? = null

        override fun setFieldValidationListener(listener: FieldValidationChange<T>): IForm.Builder<T> {
            fieldValidationListener = listener
            return this
        }

        override fun setFormValidationListener(listener: FormValidationChange): IForm.Builder<T> {
            formValidationListener = listener
            return this
        }

        override fun setValidSubmitListener(listener: ValidSubmit<T>): IForm.Builder<T> {
            validSubmitListener = listener
            return this
        }

        override fun setSubmitFailedListener(listener: SubmitFailed<T>): IForm.Builder<T> {
            submitFailedListener = listener
            return this
        }

        private val fields = mutableListOf<FormField<T, *>>()

        override fun <R> addField(key: T,
                                  input: IObservableValue<R>,
                                  validators: List<Validator<R>>,
                                  validationTriggers: List<IObservableChange>): IForm.Builder<T> {
            fields.add(FormField(key = key,
                    input = input,
                    validators = validators,
                    validationTriggers = validationTriggers))
            return this
        }

        override fun <R> addField(field: FormField<T, R>): IForm.Builder<T> {
            fields.add(field)
            return this
        }

        override fun build(): IForm {

            return Form(fieldValidationListener = fieldValidationListener,
                    formValidationListener = formValidationListener,
                    validSubmitListener = validSubmitListener,
                    submitFailedListener = submitFailedListener,
                    strategy = strategy,
                    fields = fields.toList())
        }
    }
}

