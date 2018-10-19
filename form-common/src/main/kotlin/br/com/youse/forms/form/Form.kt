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
class Form<T>(private val fieldValidationListener: IForm.FieldValidationChange<T>?,
              private val formValidationListener: FormValidationChange?,
              private val validSubmitListener: ValidSubmit<T>?,
              private val submitFailedListener: SubmitFailed<T>?,
              private val strategy: ValidationStrategy,
              private val fields: List<FormField<T, *>>) : IForm {


    private val lastFieldsMessages = mutableMapOf<T, List<ValidationMessage>>()

    private var enabledFields: List<FormField<T, *>> = fields
        get() = fields.filter { it.enabled.value ?: false }

    private var isFormValid: Boolean? = null
    private var isFormSubmitted: Boolean = false

    init {

        fields.forEach { field ->

            val validationTriggers = listOf<IObservableChange>(field.input) + field.validationTriggers
            val changeObserver = FieldChangeObserver(field)
            val enabledChangeObserver = EnabledChangeObserver(field)

            validationTriggers.forEach { validationTrigger ->
                validationTrigger.addChangeListener(observer = changeObserver)
            }

            field.enabled.addChangeListener(observer = enabledChangeObserver)

        }
    }

    private fun strategyAllowsValidation(isSubmit: Boolean): Boolean {
        return when (strategy) {
            is ValidationStrategy.AllTime -> true
            is ValidationStrategy.OnSubmit -> isSubmit
            is ValidationStrategy.AfterSubmit -> isFormSubmitted
        }
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

    private fun validateField(field: FormField<T, *>) {
        val key = field.key
        val value = field.input.value
        val validators = field.validators as List<Validator<Any?>>

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
            notifyFieldValidationChange(field, messages)
        }

        lastFieldsMessages[key] = messages
    }

    private fun notifyFieldValidationChange(field: FormField<T, *>, messages: List<ValidationMessage>) {
        field.errors.value = messages
        fieldValidationListener?.onFieldValidationChange(field.key, messages)
    }


    private fun areAllFieldValid(): Boolean {

        val allEnabledFieldsChanged = enabledFields.size == lastFieldsMessages.size

        return enabledFields.isEmpty() ||
                (allEnabledFieldsChanged && lastFieldsMessages.values
                        .map { messages -> messages.isEmpty() }
                        .reduce { acc, isValid -> acc && isValid })
    }

    private fun validateAllFields() {

        enabledFields.forEach { field ->
            validateField(field)
        }
    }

    private fun notifyValidSubmit() {

        val validFields = enabledFields
                .asSequence()
                .map {
                    Pair(it.key, it.input.value)
                }
                .toList()

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

        if (strategyAllowsValidation(true)) {
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

        private var fieldValidationListener: IForm.FieldValidationChange<T>? = null
        private var formValidationListener: FormValidationChange? = null
        private var validSubmitListener: ValidSubmit<T>? = null
        private var submitFailedListener: SubmitFailed<T>? = null

        override fun setFieldValidationListener(listener: IForm.FieldValidationChange<T>): IForm.Builder<T> {
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


    private open inner class FieldChangeObserver(protected val field: FormField<T, *>) : ChangeObserver {
        override fun onChange() {
            val enabled = field.enabled.value ?: false

            if (enabled && strategyAllowsValidation(false)) {
                validateField(field)
                notifyFormValidationChangedIfChanged()
            }
        }
    }

    private inner class EnabledChangeObserver(val field: FormField<T, *>) : ChangeObserver {
        override fun onChange() {
            val key = field.key
            val enabled = field.enabled.value ?: false

            if (enabled && strategyAllowsValidation(false) && strategy.onEnable) {
                validateField(field)
                notifyFormValidationChangedIfChanged()
            }

            if (!enabled && lastFieldsMessages.containsKey(key)) {

                lastFieldsMessages.remove(key)

                if (strategyAllowsValidation(false) && strategy.onDisable) {
                    notifyFieldValidationChange(field, emptyList())
                    notifyFormValidationChangedIfChanged()
                }
            }
        }
    }
}
