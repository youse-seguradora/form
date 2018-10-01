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
package br.com.youse.forms.samples.livedata

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import br.com.youse.forms.livedata.LiveDataForm
import br.com.youse.forms.livedata.models.LiveField
import br.com.youse.forms.validators.MinLengthValidator
import br.com.youse.forms.validators.RequiredValidator
import com.github.musichin.reactivelivedata.ReactiveLiveData
import com.snakydesign.livedataextensions.OnNextAction
import com.snakydesign.livedataextensions.doAfterNext
import com.snakydesign.livedataextensions.doBeforeNext
import com.snakydesign.livedataextensions.switchMap
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

data class LoginState(val data: Unit? = null, val error: Throwable? = null)
class LoginViewModel : ViewModel() {

    private val EMAIL_KEY = "email"
    private val PASSWORD_KEY = "password"

    val disposables = CompositeDisposable()

    val loading = MutableLiveData<Boolean>()


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

    val form = LiveDataForm.Builder<String>()
            .addField(email)
            .addField(password)
            .build()

    val submitData = MutableLiveEvent<LoginState>()

    val onSubmit = form.onValidSubmit
            .doBeforeNext(OnNextAction {
                loading.value = true
            })
            .doAfterNext(OnNextAction {
                disposables.add(submitFormToApi()
                        .subscribe({ response ->
                            submitData.postEvent(LoginState(data = response))
                        }, { error ->
                            submitData.postEvent(LoginState(error = error))
                        }))
            })
            .switchMap {
                submitData
            }
            .doAfterNext(OnNextAction {
                loading.value = false
            })

    val formEnabled = ReactiveLiveData.combineLatest(form.onFormValidationChange, loading) { isValidForm, isLoading ->
        if (isLoading == true) {
            false
        } else {
            isValidForm != false
        }
    }

    init {
        loading.value = false

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
    }
}
