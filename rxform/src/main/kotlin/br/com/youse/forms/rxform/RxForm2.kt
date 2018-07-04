package br.com.youse.forms.rxform

import br.com.youse.forms.form.IForm
import br.com.youse.forms.form.Form
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class RxForm2<T>(
        submitObservable: Observable<Unit>,
        strategy: ValidationStrategy,
        fieldObservables: Map<T, Observable<Any>>,
        fieldValidations: List<Pair<T, List<Validator<Any>>>>
) : IRxForm<T> {


    private val disposables = CompositeDisposable()

    private val submitFailed = PublishSubject.create<List<Pair<T, List<ValidationMessage>>>>()
    private val fieldValidationChange = PublishSubject.create<Pair<T, List<ValidationMessage>>>()
    private val formValidationChange = PublishSubject.create<Boolean>()
    private val validSubmit = PublishSubject.create<List<Pair<T, Any>>>()


    private val submit = if (strategy == ValidationStrategy.ALL_TIME)
        submitObservable.share().startWith(Unit)
    else
        submitObservable.share()

    init {
        val builder = Form.Builder<T>()
                .setFieldValidationListener(object : IForm.FieldValidationChange<T> {
                    override fun onChange(validation: Pair<T, List<ValidationMessage>>) {
                        fieldValidationChange.onNext(validation)
                    }
                })
                .setFormValidationListener(object : IForm.FormValidationChange {
                    override fun onChange(isValid: Boolean) {
                        formValidationChange.onNext(isValid)
                    }
                })
                .setValidSubmitListener(object : IForm.ValidSubmit<T> {
                    override fun onValidSubmit(fields: List<Pair<T, Any>>) {
                        validSubmit.onNext(fields)
                    }
                })
                .setSubmitFailedListener(object : IForm.SubmitFailed<T> {
                    override fun onValidationFailed(validations: List<Pair<T, List<ValidationMessage>>>) {
                        submitFailed.onNext(validations)
                    }
                })

        var isBuilt = false
        val observableValues = mutableMapOf<T, IForm.ObservableValue<Any>>()

        fieldObservables.forEach { (key, observable) ->

            disposables.add(
                    observable.subscribe { value ->
                        if (observableValues.containsKey(key)) {
                            observableValues[key]!!.value = value
                        } else {
                            observableValues[key] = IForm.ObservableValue(value)
                        }
                        // NOTE: Only build the real form when all observables
                        // emitted at least once.
                        if (!isBuilt && fieldValidations.size == observableValues.size) {
                            isBuilt = true

                            // NOTE: fieldValidations list has the same order of elements
                            // as the original addFieldValidations calls,
                            // so we iterate over it to create the form with the same
                            // sorting of fields.
                            fieldValidations.forEach { (key1, validators) ->
                                val field = observableValues[key1]!!
                                builder.addFieldValidations(key1, field, validators)
                            }
                            val form = builder.build()
                            disposables.add(
                                    submit.subscribe {
                                        form.doSubmit()
                                    }
                            )
                        }
                    })
        }
        if (fieldValidations.isEmpty()) {
            val form = builder.build()
            disposables.add(
                    submit.subscribe {
                        form.doSubmit()
                    }
            )
        }
    }

    override fun onFieldValidationChange(): Observable<Pair<T, List<ValidationMessage>>> {
        return fieldValidationChange
    }

    override fun onFormValidationChange(): Observable<Boolean> {
        return formValidationChange
    }

    override fun onValidSubmit(): Observable<List<Pair<T, Any>>> {
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

        private val fieldObservables = mutableMapOf<T, Observable<Any>>()
        private val fieldValidations = mutableMapOf<T, List<Validator<Any>>>()

        @Suppress("UNCHECKED_CAST")
        override fun <R> addFieldValidations(key: T, fieldObservable: Observable<R>, validators: List<Validator<R>>): IRxForm.Builder<T> {
            fieldValidations[key] = validators as List<Validator<Any>>
            fieldObservables[key] = fieldObservable as Observable<Any>
            return this
        }

        override fun build(): IRxForm<T> {


            return RxForm2(
                    submitObservable = submitObservable,
                    strategy = strategy,
                    fieldValidations = fieldValidations.map { (key, value) -> Pair(key, value) },
                    fieldObservables = fieldObservables)
        }
    }
}