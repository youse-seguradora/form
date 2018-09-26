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
package br.com.youse.forms.rxform

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class RxForm<T>(private val submit: Observable<Unit>,
                private val fieldsValidations: List<Observable<Triple<T, Any, List<ValidationMessage>>>>) : IRxForm<T> {

    override fun onFieldValidationChange(): Observable<Pair<T, List<ValidationMessage>>> {
        return Observable.merge(fieldsValidations
                .map {
                    it.map { Pair(it.first, it.third) }
                })
                .distinctUntilChanged()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onFormValidationChange(): Observable<Boolean> {
        if (fieldsValidations.isEmpty()) {
            return submit
                    .map { true }
                    .distinctUntilChanged()
        }
        return Observable.combineLatest(fieldsValidations)
        { args -> args.map { it as Triple<T, Any, List<ValidationMessage>> } }
                .map { it.filter { it.third.isNotEmpty() } }
                .map { it.isEmpty() }
                .distinctUntilChanged()
    }

    override fun onValidSubmit(): Observable<List<Pair<T, Any?>>> {
        if (fieldsValidations.isEmpty()) {
            return submit
                    .map { emptyList<Pair<T, Any>>() }
        }

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
    override fun onSubmitFailed(): Observable<List<Pair<T, List<ValidationMessage>>>> {
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

    class Builder<T>(submitObservable: Observable<Unit>,
                     strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT) : IRxForm.Builder<T> {

        private val fieldsValidations = mutableListOf<Observable<Triple<T, Any, List<ValidationMessage>>>>()

        private val submit = if (strategy == ValidationStrategy.ALL_TIME)
            submitObservable.share().startWith(Unit)
        else
            submitObservable.share()


        override fun <R> addFieldValidations(key: T, fieldObservable: Observable<R>, validators: List<Validator<R>>): IRxForm.Builder<T> {

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

        override fun build(): IRxForm<T> {
            return RxForm(submit, fieldsValidations)
        }
    }

    override fun dispose() {
        //NOTE: do nothing
    }

}
