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

import br.com.youse.forms.form.Form
import br.com.youse.forms.form.IForm.*
import br.com.youse.forms.form.models.DeferredObservableValue
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

@Suppress("UNCHECKED_CAST")
class RxForm<T>(
        submitObservable: Observable<Unit>,
        strategy: ValidationStrategy,
        fields: List<RxField<T, *>>
) : IRxForm<T> {

    private val disposables = CompositeDisposable()

    private val submitFailed = PublishSubject.create<List<Pair<T, List<ValidationMessage>>>>()
    private val fieldValidationChange = PublishSubject.create<Pair<T, List<ValidationMessage>>>()
    private val formValidationChange = PublishSubject.create<Boolean>()
    private val validSubmit = PublishSubject.create<List<Pair<T, Any?>>>()


    init {
        val builder = Form.Builder<T>(strategy = strategy)
                .setFieldValidationListener(object : FieldValidationChange<T> {
                    override fun onFieldValidationChange(key: T, validations: List<ValidationMessage>) {
                        fieldValidationChange.onNext(Pair(key, validations))
                    }
                })
                .setFormValidationListener(object : FormValidationChange {
                    override fun onFormValidationChange(isValid: Boolean) {
                        formValidationChange.onNext(isValid)
                    }
                })
                .setValidSubmitListener(object : ValidSubmit<T> {
                    override fun onValidSubmit(fields: List<Pair<T, Any?>>) {
                        validSubmit.onNext(fields)
                    }
                })
                .setSubmitFailedListener(object : SubmitFailed<T> {
                    override fun onSubmitFailed(validations: List<Pair<T, List<ValidationMessage>>>) {
                        submitFailed.onNext(validations)
                    }
                })

        fields.forEach { it ->
            val key = it.key
            val observable = it.input as Observable<Any?>
            val validators = it.validators as List<Validator<Any?>>
            val field = DeferredObservableValue<Any?>()
            builder.addField(key, field, validators)
            disposables.add(
                    observable.subscribe { value ->
                        field.setValue(value)
                    })
        }

        val form = builder.build()
        disposables.add(
                submitObservable.subscribe {
                    form.doSubmit()
                }
        )
    }

    override fun onFieldValidationChange(): Observable<Pair<T, List<ValidationMessage>>> {
        return fieldValidationChange
    }

    override fun onFormValidationChange(): Observable<Boolean> {
        return formValidationChange
    }

    override fun onValidSubmit(): Observable<List<Pair<T, Any?>>> {
        return validSubmit
    }

    override fun onSubmitFailed(): Observable<List<Pair<T, List<ValidationMessage>>>> {
        return submitFailed
    }

    override fun dispose() {
        disposables.dispose()
    }

    class Builder<T>(private val submitObservable: Observable<Unit>,
                     private val strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT) : IRxForm.Builder<T> {

        private val fields = mutableListOf<RxField<T, *>>()

        @Suppress("UNCHECKED_CAST")
        override fun <R> addField(key: T,
                                  input: Observable<R>,
                                  validators: List<Validator<R>>): IRxForm.Builder<T> {
            val field = RxField(
                    key,
                    input as Observable<Any?>,
                    validators as List<Validator<Any?>>
            )
            fields.add(field)
            return this
        }

        override fun <R> addField(field: RxField<T, R>): IRxForm.Builder<T> {
            fields.add(field)
            return this
        }

        override fun build(): IRxForm<T> {
            return RxForm(
                    submitObservable = submitObservable,
                    strategy = strategy,
                    fields = fields)
        }
    }
}