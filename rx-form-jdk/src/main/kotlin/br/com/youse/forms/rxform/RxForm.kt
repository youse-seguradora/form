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
import br.com.youse.forms.form.IForm
import br.com.youse.forms.form.IForm.*
import br.com.youse.forms.form.IObservableChange
import br.com.youse.forms.form.models.FormField
import br.com.youse.forms.form.models.ObservableChange
import br.com.youse.forms.form.models.ObservableValue
import br.com.youse.forms.rxform.models.RxField
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

@Suppress("UNCHECKED_CAST")
class RxForm<T>(
        submitObservable: Observable<Unit>,
        strategy: ValidationStrategy,
        private val fields: List<RxField<T, *>>
) : IRxForm<T> {

    private val form: IForm

    private val disposables = CompositeDisposable()

    private val submitFailed = BehaviorSubject.create<List<Pair<T, List<ValidationMessage>>>>()
    private val formValidationChange = BehaviorSubject.create<Boolean>()
    private val validSubmit = BehaviorSubject.create<List<Pair<T, Any?>>>()


    init {
        val builder = Form.Builder<T>(strategy = strategy)
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

        fields.forEach { rxField ->

            val observableInput = ObservableValue<Any?>()
            val observableErrors = ObservableValue<List<ValidationMessage>>()
            val validationTriggers = mutableListOf<IObservableChange>()

            rxField.validationTriggers.forEach { trigger ->

                val validationTrigger = ObservableChange()
                validationTriggers.add(validationTrigger)

                disposables.add(trigger.subscribe {
                    validationTrigger.notifyChange()
                })
            }

            observableErrors.addChangeListener(observer = object : IObservableChange.ChangeObserver {
                override fun onChange() {
                    val validations = observableErrors.value ?: emptyList()
                    rxField.errors.onNext(validations)
                }
            })

            val formField = FormField(key = rxField.key,
                    input = observableInput,
                    errors = observableErrors,
                    validators = rxField.validators as List<Validator<Any?>>,
                    validationTriggers = validationTriggers.toList(),
                    strategy = rxField.strategy)

            disposables.add(rxField.errors
                    .subscribe { errors ->
                        formField.errors.value = errors
                    })

            disposables.add(rxField.errors
                    .subscribe { errors ->
                        formField.errors.value = errors
                    })

            disposables.add(rxField.enabled
                    .subscribe { enabled ->
                        formField.enabled.value = enabled
                    })

            disposables.add(
                    rxField.input.subscribe { newValue ->
                        formField.input.value = newValue
                    })

            builder.addField(formField)

        }

        form = builder.build()

        disposables.add(
                submitObservable.subscribe {
                    form.doSubmit()
                }
        )
    }

    override fun reset() {
        form.reset()
    }

    override fun onFieldValidationChange(): Observable<Pair<T, List<ValidationMessage>>> {
        return Observable.merge(fields
                .map { field ->
                    field.errors.map { validations ->
                        Pair(field.key, validations)
                    }
                })
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
