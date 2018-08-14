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

import android.arch.lifecycle.LiveDataReactiveStreams
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import br.com.youse.forms.livedata.LiveDataForm
import br.com.youse.forms.validators.MinLengthValidator
import br.com.youse.forms.validators.RequiredValidator
import br.com.youse.forms.validators.ValidationStrategy
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable

class LoginViewModel : ViewModel() {

    private val EMAIL_KEY = "email"
    private val PASSWORD_KEY = "password"

    val email = MutableLiveData<CharSequence>()
    val password = MutableLiveData<CharSequence>()

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

    val form = LiveDataForm.Builder<String>(strategy = ValidationStrategy.AFTER_SUBMIT)
            .addFieldValidations(EMAIL_KEY,
                    email, emailValidations)
            .addFieldValidations(PASSWORD_KEY,
                    password,
                    passwordValidations)
            .build()

    val enabled = form.onFormValidationChange

    val onEmailValidationChange = form.onFieldValidationChange[EMAIL_KEY]!!
    val onPasswordValidationChange = form.onFieldValidationChange[PASSWORD_KEY]!!

    val success = Transformations.switchMap(form.onValidSubmit) {
        LiveDataReactiveStreams.fromPublisher(Observable.just(Unit)
                .toFlowable(BackpressureStrategy.BUFFER))
    }
}