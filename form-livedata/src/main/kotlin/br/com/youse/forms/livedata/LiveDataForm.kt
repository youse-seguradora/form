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
import br.com.youse.forms.form.IForm.DeferredObservableValue
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator


@Suppress("UNCHECKED_CAST")
class LiveDataForm<T>(
        val submit: MediatorLiveData<Boolean>,
        strategy: ValidationStrategy,
        fieldValidations: MutableMap<T, Pair<MutableLiveData<*>, List<Validator<*>>>>) {

    val onFieldValidationChange: Map<T, MutableLiveData<List<ValidationMessage>>>
    val onFormValidationChange = MutableLiveData<Boolean>()
    val onSubmitFailed = MutableLiveData<List<Pair<T, List<ValidationMessage>>>>()
    val onValidSubmit = MutableLiveData<List<Pair<T, Any?>>>()

    init {
        val fieldValidationChange = mutableMapOf<T, MutableLiveData<List<ValidationMessage>>>()

        val builder = Form.Builder<T>(strategy = strategy)
                .setFieldValidationListener(object : IForm.FieldValidationChange<T> {
                    override fun onChange(validation: Pair<T, List<ValidationMessage>>) {
                        fieldValidationChange[validation.first]!!.value = validation.second
                    }
                })
                .setFormValidationListener(object : IForm.FormValidationChange {
                    override fun onChange(isValid: Boolean) {
                        onFormValidationChange.value = isValid
                    }
                })
                .setSubmitFailedListener(object : IForm.SubmitFailed<T> {
                    override fun onValidationFailed(validations: List<Pair<T, List<ValidationMessage>>>) {
                        onSubmitFailed.value = validations
                    }
                })
                .setValidSubmitListener(object : IForm.ValidSubmit<T> {
                    override fun onValidSubmit(fields: List<Pair<T, Any?>>) {
                        onValidSubmit.value = fields
                    }
                })

        val initialValues = mutableMapOf<T, DeferredObservableValue<Any?>>()
        fieldValidations.forEach { entry ->
            val fieldKey = entry.key
            val validators = entry.value.second as List<Validator<Any?>>
            val value = DeferredObservableValue<Any?>()
            initialValues[fieldKey] = value
            fieldValidationChange[fieldKey] = MutableLiveData()
            builder.addFieldValidations(fieldKey, value, validators)

        }

        onFieldValidationChange = fieldValidationChange

        fieldValidations.forEach { it ->
            val key = it.key
            val ld = it.value.first

            this.submit.addSource(ld) {
                initialValues[key]?.setValue(it)
            }
        }

        val form = builder.build()

        this.submit.addSource(submit) {
            form.doSubmit()
        }
    }

    class Builder<T>(private val submit: MediatorLiveData<Boolean> = MediatorLiveData(),
                     private val strategy: ValidationStrategy = ValidationStrategy.AFTER_SUBMIT) {
        private val fieldValidations = mutableMapOf<T, Pair<MutableLiveData<*>, List<Validator<*>>>>()
        fun <R> addFieldValidations(key: T, field: MutableLiveData<R>, validators: List<Validator<R>>): LiveDataForm.Builder<T> {
            fieldValidations[key] = Pair(field, validators)
            return this
        }

        fun build(): LiveDataForm<T> {
            return LiveDataForm(submit, strategy, fieldValidations)
        }
    }
}
