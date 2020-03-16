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

import br.com.youse.forms.form.FormSubmissionState.*
import br.com.youse.forms.form.IForm.*
import br.com.youse.forms.form.changes.EnabledChangeObserver
import br.com.youse.forms.form.changes.FieldAllowsValidationOnChange
import br.com.youse.forms.form.changes.InputChangeObserver
import br.com.youse.forms.form.changes.TriggerChangeObserver
import br.com.youse.forms.form.models.FormField
import br.com.youse.forms.form.models.ObservableValue
import br.com.youse.forms.validators.ValidationStrategy

enum class FormSubmissionState {
    BEFORE_SUBMIT,
    ON_SUBMIT,
    AFTER_SUBMIT
}

class FormState {

    val submissionState = ObservableValue(BEFORE_SUBMIT)
    val isFormValid = ObservableValue<Boolean>()

    fun submitStateAllowsFieldValidation(strategy: ValidationStrategy): Boolean {
        return when (submissionState.value) {
            BEFORE_SUBMIT ->
                strategy.beforeSubmit
            ON_SUBMIT ->
                strategy.onSubmit
            AFTER_SUBMIT ->
                strategy.afterSubmit
            else -> false
        }
    }

}

class Form<T>(
        private val strategy: ValidationStrategy,
        private val fieldValidationListener: FieldValidationChange<T>?,
        private val formValidationListener: FormValidationChange?,
        private val validSubmitListener: ValidSubmit<T>?,
        private val submitFailedListener: SubmitFailed<T>?,
        private val formState: FormState,
        private val fields: List<FormField<T, *>>) : IForm {

    private val enabledFields: List<FormField<T, *>>
        get() = fields.filter { it.enabled.value.isTrue() }

    init {

        setupFieldsValidations()

        setupFormChangeListeners()
    }

    private fun setupFieldsValidations() {
        fields.forEach { field ->

            field.input.addChangeListener(observer = InputChangeObserver(strategy, formState, field))
            field.enabled.addChangeListener(observer = EnabledChangeObserver(strategy, formState, field) {
                validateForm()
            })

            val triggerChangeObserver = TriggerChangeObserver(strategy, formState, field)

            field.validationTriggers.forEach { validationTrigger ->
                validationTrigger.addChangeListener(observer = triggerChangeObserver)
            }

            field.errors.addChangeListener(observer = FieldAllowsValidationOnChange(strategy, formState) {
                notifyFieldValidationChange(field)
                validateForm()
            })
        }
    }

    private fun setupFormChangeListeners() {
        formState.submissionState.addChangeListener(observer = FieldAllowsValidationOnChange(strategy, formState) {
            validateAllEnabledFields()
            validateForm()
        })

        formState.isFormValid.addChangeListener(observer = FieldAllowsValidationOnChange(strategy, formState) {
            notifyFormChange()
        })
    }

    private fun validateForm() {
        formState.isFormValid.value = areAllFieldValid()
    }

    private fun areAllFieldValid(): Boolean {
        return enabledFields
                .map {
                    !it.hasErrors()
                }
                .fold(true) { acc, isValid -> acc && isValid }
    }

    private fun validateAllEnabledFields() {
        enabledFields.forEach { field ->
            field.validate()
        }
    }

    private fun notifyFieldValidationChange(field: FormField<T, *>) {
        val errors = field.errors.value ?: emptyList()
        fieldValidationListener?.onFieldValidationChange(field.key, errors)
    }

    private fun notifyFormChange() {
        val isFormValid = formState.isFormValid.value.isTrue()
        formValidationListener?.onFormValidationChange(isFormValid)
    }

    private fun notifyValidSubmit() {
        val validData = enabledFields
                .map { field ->
                    Pair(field.key, field.input.value)
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
        formState.submissionState.value = BEFORE_SUBMIT
        formState.isFormValid.value = null
        enabledFields.forEach { it.errors.value = null }
    }

    override fun doSubmit() {
        formState.submissionState.value = ON_SUBMIT

        val isFormValid = formState.isFormValid.value.isTrue()

        if (isFormValid) {
            notifyValidSubmit()
        } else {
            notifySubmitFailed()
        }

        formState.submissionState.value = AFTER_SUBMIT
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

        override fun <R> addField(field: FormField<T, R>): IForm.Builder<T> {
            fields.add(field)
            return this
        }

        override fun build(): IForm {

            return Form(fieldValidationListener = fieldValidationListener,
                    formValidationListener = formValidationListener,
                    validSubmitListener = validSubmitListener,
                    submitFailedListener = submitFailedListener,
                    formState = FormState(),
                    fields = fields.toList(),
                    strategy = strategy)
        }
    }
}


/**
 *  not null and not false
 */
internal fun Boolean?.isTrue(): Boolean = this == true