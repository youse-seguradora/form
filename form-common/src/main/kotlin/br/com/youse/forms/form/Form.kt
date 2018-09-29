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
import br.com.youse.forms.form.IObservableValidation.ValidationObserver
import br.com.youse.forms.form.IObservableValue.ValueObserver
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
              fields: List<FormField<T, *>>) : IForm {


    private val lastFieldsMessages = mutableMapOf<T, Pair<Any?, List<ValidationMessage>>>()

    private var isFormValid: Boolean? = null
    private var isFormSubmitted: Boolean? = null

    init {

        fields.forEach { field ->
            val key = field.key
            val validators = field.validators as List<Validator<Any?>>

            val observableValue = field.input as IObservableValue<Any?>

            val valueObserver = object : ValueObserver<Any?> {
                override fun onChange(value: Any?) {
                    validate(key, value, validators)
                }
            }

            observableValue.setValueListener(valueObserver = valueObserver)

            val validationObserver = object : ValidationObserver {
                override fun requestValidation() {
                    val value = lastFieldsMessages[key]?.first
                    validate(key, value, validators)
                }
            }

            field.validationTriggers.forEach { observableValidation ->
                observableValidation.addValidationListener(validationObserver = validationObserver)
            }

        }
    }

    private fun validate(key: T, value: Any?, validators: List<Validator<Any?>>) {

        val notifyListener = (strategy == ValidationStrategy.ALL_TIME)
                ||
                (strategy == ValidationStrategy.AFTER_SUBMIT && isFormSubmitted == true)

        val messages = mutableListOf<ValidationMessage>()
        for (validator in validators) {
            if (!validator.isValid(value)) {
                messages += validator.validationMessage()
            }
        }

        val lastMessages = lastFieldsMessages[key]?.second

        val hasFieldValidationChanged = messages != lastMessages

        if (notifyListener && hasFieldValidationChanged) {
            // notify field validation changed
            fieldValidationListener?.onFieldValidationChange(key, messages)
        }

        lastFieldsMessages[key] = Pair(value, messages)

        val areAllFieldsValid = areAllFieldValid()

        val hasFormValidationChanged = isFormValid != areAllFieldsValid

        if (notifyListener && hasFormValidationChanged) {
            // notify form validation changed
            formValidationListener?.onFormValidationChange(areAllFieldsValid)
        }
        isFormValid = areAllFieldsValid

    }

    private fun areAllFieldValid(): Boolean {
        return lastFieldsMessages.isEmpty() || lastFieldsMessages.values
                .map { msgs -> msgs.second.isEmpty() }
                .reduce { acc, isValid -> acc && isValid }
    }

    override fun doSubmit() {
        isFormSubmitted = true

        val areAllFieldsValid = areAllFieldValid()

        if (strategy == ValidationStrategy.AFTER_SUBMIT) {

            fieldValidationListener?.let {
                // notify field validation changed
                lastFieldsMessages.forEach { (key, pair) ->
                    fieldValidationListener.onFieldValidationChange(key, pair.second)
                }
            }
            // notify form validation changed
            formValidationListener?.onFormValidationChange(areAllFieldsValid)
        }

        if (areAllFieldsValid) {

            // notify a valid submit
            validSubmitListener?.onValidSubmit(lastFieldsMessages
                    .map { (key, pair) ->
                        Pair(key, pair.first)
                    })
        } else {

            // notify a not valid submit
            submitFailedListener?.onSubmitFailed(lastFieldsMessages
                    .filter { it.value.second.isNotEmpty() }
                    .map { (key, values) ->
                        Pair(key, values.second)
                    }
            )
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
                                  validationTriggers: List<IObservableValidation>): IForm.Builder<T> {
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

