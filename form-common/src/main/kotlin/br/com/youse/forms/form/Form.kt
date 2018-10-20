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
import br.com.youse.forms.validators.ValidationStrategy

@Suppress("UNCHECKED_CAST")
class Form<T>(private val fieldValidationListener: IForm.FieldValidationChange<T>?,
              private val formValidationListener: FormValidationChange?,
              private val validSubmitListener: ValidSubmit<T>?,
              private val submitFailedListener: SubmitFailed<T>?,
              private val strategy: ValidationStrategy,
              private val fields: List<FormField<T, *>>) : IForm {

    private var enabledFields: List<FormField<T, *>> = fields
        get() = fields.filter { it.enabled.value.isTrue() }

    private var isFormValid: Boolean? = null
    private var isFormSubmitted: Boolean = false

    init {

        fields.forEach { field ->

            if (strategy.onChange || strategy.clearErrorOnChange) {
                val inputChangeObserver = InputChangeObserver(this, strategy, field)
                field.input.addChangeListener(observer = inputChangeObserver)
            }

            if (strategy.onEnable || strategy.onDisable) {
                val enabledChangeObserver = EnabledChangeObserver(this, strategy, field)
                field.enabled.addChangeListener(observer = enabledChangeObserver)
            }

            if (strategy.onTrigger) {
                val triggerChangeObserver = TriggerChangeObserver(this, strategy, field)
                field.validationTriggers.forEach { validationTrigger ->
                    validationTrigger.addChangeListener(observer = triggerChangeObserver)
                }
            }

            fieldValidationListener?.let {
                field.errors.addChangeListener(observer = object : ChangeObserver {
                    override fun onChange() {
                        val errors = field.errors.value ?: emptyList()
                        fieldValidationListener.onFieldValidationChange(field.key, errors)
                    }
                })
            }
        }
    }

    internal fun notifyFormValidationChangedIfChanged() {

        val areAllFieldsValid = areAllFieldValid()
        val hasFormValidationChanged = isFormValid != areAllFieldsValid

        if (hasFormValidationChanged) {
            // notify form validation changed
            formValidationListener?.onFormValidationChange(areAllFieldsValid)
        }

        isFormValid = areAllFieldsValid
    }

    private fun areAllFieldValid(): Boolean {

        return enabledFields
                .map {
                    !it.hasErrors()
                }
                .fold(true) { acc, isValid ->
                    acc && isValid
                }
    }

    private fun validateAllFields() {

        enabledFields.forEach { field ->
            field.validate()
        }
    }

    private fun notifyValidSubmit() {

        val validData = enabledFields
                .map {
                    Pair(it.key, it.input.value)
                }
                .toList()

        // notify a valid submit
        validSubmitListener?.onValidSubmit(validData)
    }

    private fun notifySubmitFailed() {

        val validationMessages = enabledFields
                .filter { field -> field.hasErrors() }
                .map {
                    Pair(it.key, it.errors.value!!)
                }
                .toList()

        // notify a not valid submit
        submitFailedListener?.onSubmitFailed(validationMessages)
    }

    override fun reset() {
        isFormSubmitted = false
        isFormValid = null
    }

    override fun doSubmit() {

        isFormSubmitted = true

        if (formStateAllowsValidation()) {
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

    internal fun formStateAllowsValidation(): Boolean {
        if (!isFormSubmitted && strategy.beforeSubmit) {
            return true
        }
        if (isFormSubmitted && strategy.onSubmit) {
            return true
        }
        if (isFormSubmitted && strategy.afterSubmit) {
            return true
        }
        return false
    }

}

internal abstract class FormChangeObserver<T>(private val form: Form<T>) : ChangeObserver {
    override fun onChange() {
        if (form.formStateAllowsValidation()) {
            validateField()
            form.notifyFormValidationChangedIfChanged()
        }
    }

    abstract fun validateField()
}

internal class InputChangeObserver<T>(form: Form<T>,
                                      private val strategy: ValidationStrategy,
                                      private val field: FormField<T, *>) : FormChangeObserver<T>(form) {
    override fun validateField() {
        val enabled = field.enabled.value.isTrue()
        if (enabled) {
            if (strategy.onChange) {
                field.validate()
            }
            if (strategy.clearErrorOnChange) {
                field.errors.value = emptyList()
            }
        }
    }
}

internal class TriggerChangeObserver<T>(form: Form<T>,
                                        private val strategy: ValidationStrategy,
                                        private val field: FormField<T, *>) : FormChangeObserver<T>(form) {
    override fun validateField() {
        val enabled = field.enabled.value.isTrue()
        if (enabled) {
            if (strategy.onTrigger) {
                field.validate()
            }
        }
    }
}

internal class EnabledChangeObserver<T>(form: Form<T>,
                                        private val strategy: ValidationStrategy,
                                        private val field: FormField<T, *>) : FormChangeObserver<T>(form) {
    override fun validateField() {
        val enabled = field.enabled.value.isTrue()
        if (enabled) {
            if (strategy.onEnable) {
                field.validate()
            }
        } else {
            if (strategy.onDisable) {
                field.errors.value = emptyList()
            }
        }
    }
}

internal fun Boolean?.isTrue(): Boolean = this == true

