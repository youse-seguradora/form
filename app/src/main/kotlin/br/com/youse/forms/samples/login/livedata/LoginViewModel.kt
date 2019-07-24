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
package br.com.youse.forms.samples.login.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.live.disposable.LiveCompositeDisposable
import br.com.live.disposable.subscribe
import br.com.youse.forms.livedata.ILiveDataForm
import br.com.youse.forms.livedata.LiveDataForm
import br.com.youse.forms.livedata.models.LiveField
import br.com.youse.forms.samples.registration.RegistrationActivity
import br.com.youse.forms.validators.*
import com.github.musichin.reactivelivedata.ReactiveLiveData
import com.github.musichin.reactivelivedata.map

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

data class LoginState(val data: Unit? = null, val error: Throwable? = null)
class LoginViewModel : ViewModel() {

    private val EMAIL_KEY = "email"
    private val PASSWORD_KEY = "password"
    private val CONFIRM_PASSWORD_KEY = "confirm_password"


    val disposables = CompositeDisposable()
    val liveDisposables = LiveCompositeDisposable()
    val loading = MutableLiveData<Boolean>()

    val onSubmit = MutableLiveEvent<LoginState>()

    lateinit var formEnabled: LiveData<Boolean>

    private val emailValidations by lazy {
        listOf(RequiredValidator(
                "required"
        ))
    }

    private val passwordValidations by lazy {
        listOf(MinLengthValidator(
                "Min length 8 letters",
                8))
    }
    val email = LiveField(key = EMAIL_KEY, validators = emailValidations)
    val password = LiveField(key = PASSWORD_KEY, validators = passwordValidations)

    private val isEqualsValidator = object : Validator<String> {
        override fun validationMessage(): ValidationMessage {
            return ValidationMessage(message = "password and confirm password is not the same",
                    validationType = RegistrationActivity.PASSWORD_CONFIRMATION_MISMATCH)
        }

        override fun isValid(input: String?): Boolean {
            return input == password.input.value
        }
    }
    val confirmPassword = LiveField(key = CONFIRM_PASSWORD_KEY,
            validators = listOf(isEqualsValidator) + passwordValidations,
            validationTriggers = listOf(password.input.map { Unit }))

    lateinit var form: ILiveDataForm<String>

    // TODO: remove this method and pass the strategy by DI in the constructor
    fun createForm(strategy: ValidationStrategy) {
        form = LiveDataForm.Builder<String>(strategy = strategy)
                .addField(email)
                .addField(password)
                .addField(confirmPassword)
                .build()

        liveDisposables.clear()
        liveDisposables.add(form.onValidSubmit.subscribe {
            submit()
        })


        formEnabled = ReactiveLiveData.combineLatest(form.onFormValidationChange, loading) { isValidForm, isLoading ->
            if (isLoading == true) {
                false
            } else {
                isValidForm != false
            }
        }
    }


    init {
        loading.value = false
    }

    private fun submit() {
        disposables.add(submitFormToApi()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    loading.value = true
                }
                .doFinally {
                    loading.value = false
                }
                .subscribe({ response ->
                    onSubmit.postEvent(LoginState(data = response))
                }, { error ->
                    onSubmit.postEvent(LoginState(error = error))
                }))
    }

    private fun submitFormToApi(): Single<Unit> {
        val emailValue = email.input.value
        val passwordValue = password.input.value
        println("sending $emailValue and $passwordValue to server...")
        return Single.defer {
            Single.just(Unit)
                    .delay(1, TimeUnit.SECONDS)
        }
    }


    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        liveDisposables.clear()
    }
}
