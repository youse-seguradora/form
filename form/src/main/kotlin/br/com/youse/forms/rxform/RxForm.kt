package br.com.youse.forms.rxform

import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.Validator
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function

class RxForm<T>(private val submit: Observable<Unit>, private val fieldsValidations: List<Observable<Triple<T, Any, List<ValidationMessage>>>>) {

    fun onFieldValidationChange(): Observable<Pair<T, List<ValidationMessage>>> {
        return Observable.merge(fieldsValidations
                .map {
                    it.map { Pair(it.first, it.third) }
                })
                .distinctUntilChanged()
    }

    @Suppress("UNCHECKED_CAST")
    fun onFormValidationChange(): Observable<Boolean> {
        if (fieldsValidations.isEmpty()) {
            return submit.map { true }
        }
        return Observable.combineLatest(fieldsValidations)
        { args -> args.map { it as Triple<T, Any, List<ValidationMessage>> } }
                .map { it.filter { it.third.isNotEmpty() } }
                .map { it.isEmpty() }
                .distinctUntilChanged()
    }

    fun onValidSubmit(): Observable<List<Pair<T, Any>>> {
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

    @Suppress("UNCHECKED_CAST")
    fun firstFieldValidationFailed(): Observable<Pair<T, List<ValidationMessage>>> {
        val combined = Observable.combineLatest(fieldsValidations)
        { args -> args.map { it as Triple<T, Any, List<ValidationMessage>> } }
                .map { it.filter { it.third.isNotEmpty() } }
                .filter { !it.isEmpty() }
                .map { it.first() }
                .map { Pair(it.first, it.third) }
                .distinctUntilChanged()
        return submit
                .startWith(Unit)
                .switchMap { combined }
    }

    class Builder<T>(submitObservable: Observable<Unit>, strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT) {

        private val fieldsValidations = mutableListOf<Observable<Triple<T, Any, List<ValidationMessage>>>>()

        private val submit = if (strategy == ValidationStrategy.ALL_TIME)
            submitObservable.share().startWith(Unit)
        else
            submitObservable.share()

        fun <R> addFieldValidations(key: T, field: Observable<R>, validators: List<Validator<R>>): Builder<T> {

            val fieldValidationObservable = Observable.combineLatest(field, submit,
                    BiFunction { t1: R, _: Unit ->
                        t1
                    })
                    .map {
                        val messages = mutableListOf<ValidationMessage>()
                        for (validator in validators) {
                            if (!validator.isValid(it)) {
                                messages += validator.validationMessage()
                            }
                        }
                        Triple(key, it as Any, messages.toList())
                    }

            fieldsValidations.add(fieldValidationObservable)
            return this
        }

        fun build(): RxForm<T> {
            return RxForm(submit, fieldsValidations)
        }
    }
}

