package br.com.youse.forms.livedata

import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import br.com.youse.forms.validators.MinLengthValidator
import br.com.youse.forms.validators.RequiredValidator
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

class LoginViewModel : ViewModel() {

    val EMAIL = "email"
    val PASSWORD = "password"

    val email = MutableLiveData<String>()

    val password = MutableLiveData<String>()


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

    val form = LiveDataForm.Builder<String>()
            .addFieldValidations(EMAIL,
                    email, emailValidations)
            .addFieldValidations(PASSWORD,
                    password,
                    passwordValidations)
            .build()

    val success = Transformations.switchMap(form.onValidSubmit) {
        LiveDataReactiveStreams.fromPublisher(Observable.just(true).toFlowable(BackpressureStrategy.BUFFER))
    }

}
