package br.com.youse.forms.form

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator


class Form<T>(private val fieldValidationListener: IForm.FieldValidationChange<T>?,
              private val formValidationListener: IForm.FormValidationChange?,
              private val validSubmitListener: IForm.ValidSubmit<T>?,
              private val strategy: ValidationStrategy,
              fieldValidations: Map<T, Pair<IForm.ObservableValue<*>, List<Validator<*>>>>) : IForm<T> {


    private val lastFieldsMessages = mutableMapOf<T, ValidationMessages>()
    private var isFormValid: Boolean? = null
    private var isFormSubmitted: Boolean? = null

    init {

        fieldValidations.forEach { key, pair ->
            val observableValue = pair.first as IForm.ObservableValue<Any>
            val validators = pair.second as List<Validator<Any>>
            val listener = object : IForm.ObservableValue.ValueObserver<Any> {
                override fun onChange(value: Any) {
                    val notifyListener = (strategy == ValidationStrategy.ALL_TIME)
                            ||
                            (strategy == ValidationStrategy.AFTER_SUBMIT && isFormSubmitted == true)

                    val messages = mutableListOf<ValidationMessage>()
                    for (validator in validators) {
                        if (!validator.isValid(value)) {
                            messages += validator.validationMessage()
                        }
                    }
                    // verify if notify field validation changed
                    val validationMessage = Pair(value as Any, messages)

                    val isFieldValid = validationMessage.isValid()
                    val wasFieldValid = lastFieldsMessages[key]?.isValid()
                    val hasFieldValidationChanged = wasFieldValid != isFieldValid

                    if (notifyListener && hasFieldValidationChanged) {
                        fieldValidationListener?.onChange(key, messages)
                    }
                    lastFieldsMessages[key] = validationMessage

                    // verify if notify form validation changed
                    val areAllFieldsValid = lastFieldsMessages.values
                            .map { msgs -> msgs.isValid() }
                            .reduce { acc, isValid -> acc && isValid }

                    val hasFormValidationChanged = isFormValid != areAllFieldsValid

                    if (notifyListener && hasFormValidationChanged) {
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

        // notify field validation changed
        lastFieldsMessages.forEach { key, values ->
            fieldValidationListener?.onChange(key, values.second)
        }

        // notify form validation changed
        val areAllFieldsValid = lastFieldsMessages.values
                .map { it.isValid() }
                .reduce { acc, isValid -> acc && isValid }
        formValidationListener?.onChange(areAllFieldsValid)


        // notify a valid submit
        if (areAllFieldsValid) {
            validSubmitListener?.onValidSubmit(lastFieldsMessages
                    .map { (key, values) ->
                        Pair(key, values.first)
                    })
        }
    }


    class Builder<T>(private val strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT) : IForm.Builder<T> {
        private var fieldValidationListener: IForm.FieldValidationChange<T>? = null
        private var formValidationListener: IForm.FormValidationChange? = null
        private var validSubmitListener: IForm.ValidSubmit<T>? = null

        override fun setFieldValidationListener(listener: IForm.FieldValidationChange<T>): Builder<T> {
            fieldValidationListener = listener
            return this
        }

        override fun setFormValidationListener(listener: IForm.FormValidationChange): Builder<T> {
            formValidationListener = listener
            return this
        }

        override fun setValidSubmitListener(listener: IForm.ValidSubmit<T>): Builder<T> {
            validSubmitListener = listener
            return this
        }


        private val fieldValidations = mutableMapOf<T, Pair<IForm.ObservableValue<*>, List<Validator<*>>>>()

        override fun <R> addFieldValidations(key: T, observableValue: IForm.ObservableValue<R>, validators: List<Validator<R>>): IForm.Builder<T> {
            fieldValidations[key] = Pair(observableValue, validators)
            return this
        }


        override fun build(): IForm<T> {

            return Form(fieldValidationListener = fieldValidationListener,
                    formValidationListener = formValidationListener,
                    validSubmitListener = validSubmitListener,
                    strategy = strategy,
                    fieldValidations = fieldValidations)
        }

    }
}

private typealias ValidationMessages = Pair<Any, List<ValidationMessage>>

private fun ValidationMessages.isValid(): Boolean {
    return second.isEmpty()
}

