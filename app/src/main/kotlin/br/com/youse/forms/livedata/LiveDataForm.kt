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
import br.com.youse.forms.validators.Validator

class LiveDataForm<T>(val submit: MediatorLiveData<Unit>, fieldValidations: MutableMap<T, Pair<MutableLiveData<*>, List<Validator<*>>>>) {

    val onFieldValidationChange = MutableLiveData<Pair<T, List<ValidationMessage>>>()
    val onFormValidationChange = MutableLiveData<Boolean>()
    val onSubmitFailed = MutableLiveData<List<Pair<T, List<ValidationMessage>>>>()
    val onValidSubmit = MutableLiveData<List<Pair<T, Any>>>()

    init {
        val builder = Form.Builder<T>()
                .setFieldValidationListener(object : IForm.FieldValidationChange<T> {
                    override fun onChange(validation: Pair<T, List<ValidationMessage>>) {
                        onFieldValidationChange.value = validation
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
                    override fun onValidSubmit(fields: List<Pair<T, Any>>) {
                        onValidSubmit.postValue(fields)
                    }
                })

        val map = mutableMapOf<T, IForm.ObservableValue<Any>>()
        var isBuilt = false
        fieldValidations.forEach { key, values ->
            val ld = values.first
            val validators = values.second

            this.submit.addSource(ld) {

                if (!map.containsKey(key)) {
                    val value = IForm.ObservableValue(it!!)
                    builder.addFieldValidations(key, value, validators as List<Validator<Any>>)
                    map[key] = value
                } else {
                    map[key]!!.value = it!!
                }
                if (map.size == fieldValidations.size && !isBuilt) {
                    isBuilt = true
                    val form = builder.build()
                    this.submit.addSource(submit) {
                        form.doSubmit()
                    }
                }
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
        @BindingAdapter(value = ["owner", "formFieldKey", "onFieldValidationChange"], requireAll = true)
        fun <T> onFieldValidationChange(view: TextInputLayout, owner: LifecycleOwner,
                                        formFieldKey: T,
                                        ld: MutableLiveData<Pair<T, List<ValidationMessage>>>) {
            ld.observe(owner, Observer<Pair<T, List<ValidationMessage>>> { t ->
                if (t?.first == formFieldKey) {
                    view.error = t?.second?.joinToString { it.message }
                }
            })
        }


        @BindingAdapter(value = "formField")
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

    class Builder<T>(private val submit: MediatorLiveData<Unit> = MediatorLiveData()) {
        private val fieldValidations = mutableMapOf<T, Pair<MutableLiveData<*>, List<Validator<*>>>>()
        fun <R> addFieldValidations(key: T, field: MutableLiveData<R>, validators: List<Validator<R>>): LiveDataForm.Builder<T> {
            fieldValidations.put(key, Pair(field, validators))
            return this
        }

        fun build(): LiveDataForm<T> {
            return LiveDataForm(submit, fieldValidations)
        }
    }
}