package br.com.youse.forms.livedata

import android.arch.lifecycle.*
import br.com.youse.forms.validators.MinLengthValidator
import br.com.youse.forms.validators.RequiredValidator
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

class LoginViewModel : ViewModel() {

    val email = MutableLiveData<String>()

    val password = MutableLiveData<String>()

    val submit = MediatorLiveData<Unit>()

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

    val form = LiveDataForm.Builder<String>(submit)
            .addFieldValidations("Email",
                    email, emailValidations)
            .addFieldValidations("Password",
                    password,
                    passwordValidations)
            .build()

    val success = Transformations.switchMap(form.onValidSubmit) {
        LiveDataReactiveStreams.fromPublisher(Observable.just(true).toFlowable(BackpressureStrategy.BUFFER))
    }

}
