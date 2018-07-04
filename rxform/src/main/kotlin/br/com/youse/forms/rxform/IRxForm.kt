package br.com.youse.forms.rxform

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.Validator
import io.reactivex.Observable

interface IRxForm<T> {

    /**
     * Emits every time a field validation changes.
     * Each emission contains the field key and a list of validation messages,
     * if the validation messages list is empty the field it valid.
     */
    fun onFieldValidationChange(): Observable<Pair<T, List<ValidationMessage>>>

    /**
     * Emits every time the form validation changes.
     * Each emission contains a boolean indicating if the form is valid (true) or not (false).
     */
    fun onFormValidationChange(): Observable<Boolean>

    /**
     * Emits only when a submit happens and the form is valid.
     * Emits a list of Pairs, each one with the field key and the current value of that field.
     * NOTE: As each field can be of a different type we need to use Any here.
     */
    fun onValidSubmit(): Observable<List<Pair<T, Any>>>

    /**
     * Emits every time a submit validation fails and return only failed fields keys and validations.
     *  This is useful to scroll or give focus to the given field.
     */
    fun onSubmitFailed(): Observable<List<Pair<T, List<ValidationMessage>>>>

    /**
     * Dispose internal subscriptions
     */
    fun dispose()

    /**
     * Builder to create an RxForm, it takes a submit observable that emits when the user submits the form
     * and optionally a validation strategy (AFTER_SUBMIT by default).
     */
    interface Builder<T> {

        /**
         * Adds a field to the builder, it takes a key to identify the field,
         * a field observable that emits the field value changes and a list
         * of validators for that field.
         */
        fun <R> addFieldValidations(key: T, fieldObservable: Observable<R>, validators: List<Validator<R>>): Builder<T>

        /**
         *  Builds the form.
         */
        fun build(): IRxForm<T>
    }
}