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
        fieldValidations: Map<T, Pair<Observable<*>, List<Validator<*>>>>
) : IRxForm<T> {


    private val disposables = CompositeDisposable()

    private val submitValidationFailed = PublishSubject.create<List<Pair<T, List<ValidationMessage>>>>()
    private val fieldValidationChange = PublishSubject.create<Pair<T, List<ValidationMessage>>>()
    private val formValidationChange = PublishSubject.create<Boolean>()
    private val validSubmit = PublishSubject.create<List<Pair<T, Any>>>()


    private val submit = if (strategy == ValidationStrategy.ALL_TIME)
        submitObservable.share().startWith(Unit)
    else
        submitObservable.share()

    private val builder = Form.Builder<T>()
            .setFieldValidationListener(object : IForm.FieldValidationChange<T> {
                override fun onChange(messages: Pair<T, List<ValidationMessage>>) {
                    fieldValidationChange.onNext(messages)
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
            .setSubmitValidationFailedListener(object : IForm.SubmitValidationFailed<T> {
                override fun onValidationFailed(validations: List<Pair<T, List<ValidationMessage>>>) {
                    submitValidationFailed.onNext(validations)
                }
            })
    private val observableValues = mutableMapOf<T, IForm.ObservableValue<*>>()

    init {

        fieldValidations.forEach { key, pair ->

            val fieldObservable = pair.first as Observable<Any>
            val validators = pair.second as List<Validator<Any>>
            disposables.add(
                    fieldObservable.subscribe {
                        if (observableValues.containsKey(key)) {
                            val field = observableValues[key]!! as IForm.ObservableValue<Any>
                            field.value = it
                        } else {
                            val field = IForm.ObservableValue(it)
                            observableValues[key] = field
                            builder.addFieldValidations(key, field, validators)
                        }
                    })

        }
        val form = builder.build()
        disposables.add(
                submit.subscribe {
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

    override fun onValidSubmit(): Observable<List<Pair<T, Any>>> {
        return validSubmit
    }

    override fun onSubmitValidationFailed(): Observable<List<Pair<T, List<ValidationMessage>>>> {
        return submitValidationFailed
    }

    override fun dispose() {
        disposables.dispose()
    }

    class Builder<T>(private val submitObservable: Observable<Unit>,
                     private val strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT) : IRxForm.Builder<T> {

        private val fieldValidations = mutableMapOf<T, Pair<Observable<*>, List<Validator<*>>>>()

        override fun <R> addFieldValidations(key: T, fieldObservable: Observable<R>, validators: List<Validator<R>>): IRxForm.Builder<T> {
            fieldValidations[key] = Pair(fieldObservable, validators)
            return this
        }

        override fun build(): IRxForm<T> {


            return RxForm2(
                    submitObservable = submitObservable,
                    strategy = strategy,
                    fieldValidations = fieldValidations)
        }
    }
}