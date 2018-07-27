package br.com.youse.forms.livedata

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.com.youse.forms.validators.MinLengthValidator
import br.com.youse.forms.validators.RequiredValidator

class LoginViewModel : ViewModel() {

    val email = MutableLiveData<String>()

    val password = MutableLiveData<String>()

    val submit = MutableLiveData<Unit>()

    private val emailValidations by lazy {
        listOf(RequiredValidator(
                "required"
        ))
    }
    private val passwordValidations by lazy {
        listOf(MinLengthValidator(
                "Min length",
                8))
    }

    fun onSubmit() {
        submit.postValue(Unit)
    }

    val form = LiveDataForm.Builder<String>(submit)
            .addFieldValidations("Email",
                    email, emailValidations)
            .addFieldValidations("Password",
                    password,
                    passwordValidations)
            .build()

}
