package br.com.youse.forms.rxform

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.Validator
import io.reactivex.Observable

interface IRxForm<T> {

    fun onFieldValidationChange(): Observable<Pair<T, List<ValidationMessage>>>

    fun onFormValidationChange(): Observable<Boolean>

    fun onValidSubmit(): Observable<List<Pair<T, Any>>>

    fun onSubmitValidationFailed(): Observable<List<Pair<T, List<ValidationMessage>>>>

    fun dispose()

    interface Builder<T> {
        fun <R> addFieldValidations(key: T, fieldObservable: Observable<R>, validators: List<Validator<R>>): Builder<T>
        fun build(): IRxForm<T>
    }
}