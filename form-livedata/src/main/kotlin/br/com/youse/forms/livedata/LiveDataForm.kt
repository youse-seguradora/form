package br.com.youse.forms.livedata

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.databinding.BindingAdapter
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import br.com.youse.forms.form.Form
import br.com.youse.forms.form.IForm
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.Validator

@Suppress("UNCHECKED_CAST")
class LiveDataForm<T>(
        val submit: MediatorLiveData<Unit>,
        strategy: ValidationStrategy,
        fieldValidations: MutableMap<T, Pair<MutableLiveData<*>, List<Validator<*>>>>) {

    val onFieldValidationChange = mutableMapOf<T, MutableLiveData<List<ValidationMessage>>>()
    val onFormValidationChange = MutableLiveData<Boolean>()
    val onSubmitFailed = MutableLiveData<List<Pair<T, List<ValidationMessage>>>>()
    val onValidSubmit = MutableLiveData<List<Pair<T, Any?>>>()

    init {
        val builder = Form.Builder<T>(strategy = strategy)
                .setFieldValidationListener(object : IForm.FieldValidationChange<T> {
                    override fun onChange(validation: Pair<T, List<ValidationMessage>>) {
                        onFieldValidationChange[validation.first]!!.value = validation.second
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

        val initialValues = mutableMapOf<T, IForm.ObservableValue<Any?>>()
        fieldValidations.forEach { entry ->
            val fieldKey = entry.key
            val validators = entry.value.second as List<Validator<Any?>>
            val value = IForm.ObservableValue(null as Any?)

            initialValues[fieldKey] = value
            onFieldValidationChange[fieldKey] = MutableLiveData()
            builder.addFieldValidations(fieldKey, value, validators)

        }
        val form = builder.build()
        this.submit.addSource(submit) {
            form.doSubmit()
        }

        fieldValidations.forEach { it ->
            val key = it.key
            val ld = it.value.first

            this.submit.addSource(ld) {
                initialValues[key]?.value = it
            }
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["owner", "formSubmit"], requireAll = true)
        fun onSubmit(view: View, owner: LifecycleOwner, ld: MediatorLiveData<Unit>) {
            ld.observe(owner, Observer<Unit> { })
            view.setOnClickListener {
                ld.postValue(Unit)
            }
        }


        @JvmStatic
        @BindingAdapter(value = ["onFieldValidationChange"], requireAll = true)
        fun onFieldValidationChange(view: TextInputLayout,
                                    validations: List<ValidationMessage>) {
            view.error = validations.firstOrNull()?.message
        }


        @BindingAdapter(value = ["formField"])
        @JvmStatic
        fun formField(view: EditText, ld: MutableLiveData<String>) {
            view.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                    ld.postValue(view.text.toString())
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            })
            ld.postValue(view.text.toString())
        }
    }

    class Builder<T>(private val submit: MediatorLiveData<Unit> = MediatorLiveData(),
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
