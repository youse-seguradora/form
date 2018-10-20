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
package br.com.youse.forms.livedata

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import br.com.youse.forms.form.Form
import br.com.youse.forms.form.IForm
import br.com.youse.forms.form.IObservableChange
import br.com.youse.forms.form.models.FormField
import br.com.youse.forms.form.models.ObservableValue
import br.com.youse.forms.form.models.ObservableChange
import br.com.youse.forms.livedata.models.LiveField
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator


@Suppress("UNCHECKED_CAST")
class LiveDataForm<T>(
        override val onFormValidationChange: MediatorLiveData<Boolean>,
        strategy: ValidationStrategy,
        private val fields: List<LiveField<T, *>>) : ILiveDataForm<T> {


    private var form: IForm

    override val onSubmitFailed = MutableLiveData<List<Pair<T, List<ValidationMessage>>>>()
    override val onValidSubmit = MutableLiveData<Unit>()
    override val onFieldValidationChange: MutableLiveData<Pair<T, List<ValidationMessage>>>
        get() {
            val mediator = MediatorLiveData<Pair<T, List<ValidationMessage>>>()
            fields.forEach { liveField ->
                mediator.addSource(liveField.errors) { validations ->
                    validations?.let {
                        mediator.value = Pair(liveField.key, validations)
                    }
                }
            }
            return mediator
        }


    init {

        val builder = Form.Builder<T>(strategy = strategy)
                .setFormValidationListener(object : IForm.FormValidationChange {
                    override fun onFormValidationChange(isValid: Boolean) {
                        onFormValidationChange.value = isValid
                    }
                })
                .setSubmitFailedListener(object : IForm.SubmitFailed<T> {
                    override fun onSubmitFailed(validations: List<Pair<T, List<ValidationMessage>>>) {
                        onSubmitFailed.value = validations
                    }
                })
                .setValidSubmitListener(object : IForm.ValidSubmit<T> {
                    override fun onValidSubmit(fields: List<Pair<T, Any?>>) {
                        onValidSubmit.value = Unit
                    }
                })


        fields.forEach { liveField ->
            val key = liveField.key
            val input = liveField.input as MutableLiveData<Any?>
            val validators = liveField.validators as List<Validator<Any?>>
            val errors = liveField.errors

            val observableInput = ObservableValue(input.value)
            val observableErrors = ObservableValue<List<ValidationMessage>>()
            val validationTriggers = mutableListOf<IObservableChange>()

            liveField.validationTriggers.forEach { trigger ->

                val validationTrigger = ObservableChange()
                validationTriggers.add(validationTrigger)

                onFormValidationChange.addSource(trigger) {
                    validationTrigger.notifyChange()
                }
            }

            observableErrors.addChangeListener(observer = object : IObservableChange.ChangeObserver {
                override fun onChange() {
                    errors.value = observableErrors.value
                }
            })

            val formField = FormField(key = key,
                    input = observableInput,
                    errors = observableErrors,
                    validators = validators,
                    validationTriggers = validationTriggers.toList())


            onFormValidationChange.addSource(liveField.enabled) { enabled ->
                formField.enabled.value = enabled
            }

            onFormValidationChange.addSource(input) { newValue ->
                formField.input.value = newValue
            }

            builder.addField(formField)

        }

        form = builder.build()

    }

    override fun reset() {
        form.reset()
    }

    override fun doSubmit() = form.doSubmit()

    class Builder<T>(private val submit: MediatorLiveData<Boolean> = MediatorLiveData(),
                     private val strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT) : ILiveDataForm.Builder<T> {
        private val fields = mutableListOf<LiveField<T, *>>()

        override fun <R> addField(field: LiveField<T, R>): LiveDataForm.Builder<T> {
            fields.add(field)
            return this
        }

        override fun build(): ILiveDataForm<T> {
            return LiveDataForm(submit, strategy, fields)
        }
    }
}
