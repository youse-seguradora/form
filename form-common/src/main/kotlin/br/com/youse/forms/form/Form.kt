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
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator


@Suppress("UNCHECKED_CAST")
class Form<T>(private val fieldValidationListener: FieldValidationChange<T>?,
              private val formValidationListener: FormValidationChange?,
              private val validSubmitListener: ValidSubmit<T>?,
              private val submitFailedListener: SubmitFailed<T>?,
              private val strategy: ValidationStrategy,
              fieldValidations: Map<T, Pair<IObservableValue<*>, List<Validator<*>>>>) : IForm<T> {


    private val lastFieldsMessages = mutableMapOf<T, Pair<Any?, List<ValidationMessage>>>()

    private var isFormValid: Boolean? = null
    private var isFormSubmitted: Boolean? = null

    init {

        fieldValidations.forEach { it ->
            val key = it.key
            val pair = it.value
            val observableValue = pair.first as IObservableValue<Any?>
            val validators = pair.second as List<Validator<Any?>>
            val listener = object : IObservableValue.ValueObserver<Any?> {
                override fun onChange(value: Any?) {

                    val notifyListener = (strategy == ValidationStrategy.ALL_TIME)
                            ||
                            (strategy == ValidationStrategy.AFTER_SUBMIT && isFormSubmitted == true)

                    val messages = mutableListOf<ValidationMessage>()
                    for (validator in validators) {
                        if (!validator.isValid(value)) {
                            messages += validator.validationMessage()
                        }
                    }

                    val validationMessage = Pair(value, messages)

                    val isFieldValid = validationMessage.second.isEmpty()
                    val wasFieldValid = lastFieldsMessages[key]?.second?.isEmpty()
                    val hasFieldValidationChanged = wasFieldValid != isFieldValid

                    if (notifyListener && hasFieldValidationChanged) {
                        // notify field validation changed
                        fieldValidationListener?.onChange(Pair(key, messages))
                    }
                    lastFieldsMessages[key] = validationMessage

                    val areAllFieldsValid = lastFieldsMessages.isEmpty() || lastFieldsMessages.values
                            .map { msgs -> msgs.second.isEmpty() }
                            .reduce { acc, isValid -> acc && isValid }

                    val hasFormValidationChanged = isFormValid != areAllFieldsValid

                    if (notifyListener && hasFormValidationChanged) {
                        // notify form validation changed
                        formValidationListener?.onChange(areAllFieldsValid)
                    }
                    isFormValid = areAllFieldsValid
                }
            }

            observableValue.setValueListener(valueObserver = listener)
        }
    }

    override fun doSubmit() {
        isFormSubmitted = true

        val areAllFieldsValid = lastFieldsMessages.isEmpty() || lastFieldsMessages.values
                .map { it.second.isEmpty() }
                .reduce { acc, isValid -> acc && isValid }

        if (strategy == ValidationStrategy.AFTER_SUBMIT) {

            // notify field validation changed
            lastFieldsMessages.forEach { it ->
                val key = it.key
                val values = it.value
                fieldValidationListener?.onChange(Pair(key, values.second))
            }

            // notify form validation changed
            formValidationListener?.onChange(areAllFieldsValid)
        }

        if (areAllFieldsValid) {

            // notify a valid submit
            validSubmitListener?.onValidSubmit(lastFieldsMessages
                    .map { (key, values) ->
                        Pair(key, values.first)
                    })
        } else {

            // notify a not valid submit
            submitFailedListener?.onValidationFailed(lastFieldsMessages
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

        private val fieldValidations = mutableMapOf<T, Pair<IObservableValue<*>, List<Validator<*>>>>()

        override fun <R> addFieldValidations(key: T,
                                             observableValue: IObservableValue<R>,
                                             validators: List<Validator<R>>): IForm.Builder<T> {
            fieldValidations[key] = Pair(observableValue, validators)
            return this
        }


        override fun build(): IForm<T> {

            return Form(fieldValidationListener = fieldValidationListener,
                    formValidationListener = formValidationListener,
                    validSubmitListener = validSubmitListener,
                    submitFailedListener = submitFailedListener,
                    strategy = strategy,
                    fieldValidations = fieldValidations)
        }

    }
}

