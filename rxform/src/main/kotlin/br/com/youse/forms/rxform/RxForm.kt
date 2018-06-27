package br.com.youse.forms.rxform

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class RxForm<T>(private val submit: Observable<Unit>,
                private val fieldsValidations: List<Observable<Triple<T, Any, List<ValidationMessage>>>>) : IRxForm<T> {
    override fun dispose() {
        // do nothing
    }

    /**
     * Emitts every time a field validation changes.
     * Each emission contains the field key and a list of validation messages,
     * if the list is empty the field it valid.
     */
    override fun onFieldValidationChange(): Observable<Pair<T, List<ValidationMessage>>> {
        return Observable.merge(fieldsValidations
                .map {
                    it.map { Pair(it.first, it.third) }
                })
                .distinctUntilChanged()
    }

    /**
     * Emits every time the form validation changes.
     * Ech emission contains a boolean indicating if the form is valid (true) or not (false).
     */

    @Suppress("UNCHECKED_CAST")
    override fun onFormValidationChange(): Observable<Boolean> {
        if (fieldsValidations.isEmpty()) {
            return submit.map { true }
        }
        return Observable.combineLatest(fieldsValidations)
        { args -> args.map { it as Triple<T, Any, List<ValidationMessage>> } }
                .map { it.filter { it.third.isNotEmpty() } }
                .map { it.isEmpty() }
                .distinctUntilChanged()
    }

    /**
     * Emits only when a submit happens and the form is valid.
     * Emits a list of Pairs, each one with the field key and the current value of that field.
     * NOTE: As each field can be of a different type we need to use Any here.
     */
    override fun onValidSubmit(): Observable<List<Pair<T, Any>>> {
        return submit.withLatestFrom(onFormValidationChange(),
                BiFunction { _: Unit, isValidForm: Boolean -> isValidForm })
                .filter { it }
                .map { Unit }
                .withLatestFrom(
                        Observable.combineLatest(fieldsValidations.map {
                            it.map { Pair(it.first, it.second) }
                        }) { list: Array<Any> ->
                            list.filterIsInstance<Pair<T, Any>>()
                        }, BiFunction { _: Unit, formData: List<Pair<T, Any>> -> formData }
                )
    }

    /**
     * Emits every time a submit validation fails and return only failed fields and validations.
     *  This is useful to scroll to the given field.
     */
    @Suppress("UNCHECKED_CAST")
    override fun onSubmitValidationFailed(): Observable<List<Pair<T, List<ValidationMessage>>>> {
        val combined = Observable.combineLatest(fieldsValidations)
        { args -> args.map { it as Triple<T, Any, List<ValidationMessage>> } }
                .map { it.filter { it.third.isNotEmpty() } }
                .filter { !it.isEmpty() }
                .map { list -> list.map { Pair(it.first, it.third) } }
                .distinctUntilChanged()
        return submit
                .startWith(Unit)
                .switchMap { combined }
    }

    /**
     * Builder to create an RxForm, it takes a submit observable that emits when the user submits the form
     * and optionally a validation strategy (AFTER_SUBMIT by default).
     */
    class Builder<T>(submitObservable: Observable<Unit>,
                     strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT) : IRxForm.Builder<T> {

        private val fieldsValidations = mutableListOf<Observable<Triple<T, Any, List<ValidationMessage>>>>()

        private val submit = if (strategy == ValidationStrategy.ALL_TIME)
            submitObservable.share().startWith(Unit)
        else
            submitObservable.share()

        /**
         * Adds a field to the form, it takes a key to identify the field,
         * a field observable that emits the field value changes and a list
         * of validators for that field.
         */
        override fun <R> addFieldValidations(key: T, fieldObservable: Observable<R>, validators: List<Validator<R>>): RxForm.Builder<T> {

            val fieldValidationObservable = Observable.combineLatest(fieldObservable, submit,
                    BiFunction { t1: R, _: Unit ->
                        t1
                    })
                    .map { value ->
                        val messages = mutableListOf<ValidationMessage>()
                        for (validator in validators) {
                            if (!validator.isValid(value)) {
                                messages += validator.validationMessage()
                            }
                        }
                        Triple(key, value as Any, messages.toList())
                    }

            fieldsValidations.add(fieldValidationObservable)
            return this
        }

        /**
         *  Builds the form.
         */
        override fun build(): IRxForm<T> {
            return RxForm(submit, fieldsValidations)
        }
    }
}

