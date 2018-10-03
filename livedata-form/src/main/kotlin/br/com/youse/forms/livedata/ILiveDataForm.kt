package br.com.youse.forms.livedata

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import br.com.youse.forms.livedata.models.LiveField
import br.com.youse.forms.validators.ValidationMessage

interface ILiveDataForm<T> {
    val onFormValidationChange: MediatorLiveData<Boolean>
    val onSubmitFailed: MutableLiveData<List<Pair<T, List<ValidationMessage>>>>
    val onValidSubmit: MutableLiveData<Unit>
    val onFieldValidationChange: MutableLiveData<Pair<T, List<ValidationMessage>>>

    fun doSubmit()

    interface Builder<T> {

        fun <R> addField(field: LiveField<T, R>): LiveDataForm.Builder<T>
        fun build(): ILiveDataForm<T>
    }

}