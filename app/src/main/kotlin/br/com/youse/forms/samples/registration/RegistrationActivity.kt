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
package br.com.youse.forms.samples.registration

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import br.com.youse.forms.R
import br.com.youse.forms.rxform.IRxForm
import br.com.youse.forms.rxform.RxForm
import br.com.youse.forms.rxform.models.RxField
import br.com.youse.forms.validators.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_registration.*

class Key(val id: Int, val serverKey: String)


class RegistrationActivity : AppCompatActivity() {

    lateinit var rxForm: IRxForm<Key>

    private val disposables = CompositeDisposable()
    private val passwordValidators = listOf(
            MinLengthValidator("The password should be at least 8 letters long", 8)
    )

    private val ageValidators = listOf(
            MinValueValidator("You should be at least 21 old to register", 21)
    )

    private val isEqualsValidator = object : Validator<String> {
        override fun validationMessage(): ValidationMessage {
            return ValidationMessage(message = "password and confirm password is not the same",
                    validationType = PASSWORD_CONFIRMATION_MISMATCH)
        }

        override fun isValid(input: String?): Boolean {
            return input == password.text.toString()
        }
    }

    private val requireTrueValidator = object : Validator<Boolean> {
        override fun validationMessage(): ValidationMessage {
            return ValidationMessage(
                    message = "You need to accept the terms...",
                    validationType = TERMS_ACCEPTANCE)
        }

        override fun isValid(input: Boolean?): Boolean {
            return input == true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        disposables.add(ageSwitch.checkedChanges()
                .subscribe {
                    ageInputLayout.isEnabled = it
                })

        val ageChanges = age.ageChanges()
        val ageEnabledChange = age.onEnabledChange

        val ageField = RxField(key = Key(ageInputLayout.id, "age"),
                input = ageChanges,
                enabled = ageEnabledChange,
                validators = ageValidators)

        val passwordChanges = password.textChanges()
                .map { it.toString() }
                .share()

        val passwordField = RxField(key = Key(passwordInputLayout.id, "password"),
                input = passwordChanges,
                validators = passwordValidators)

        val confirmPasswordField = RxField(key = Key(passwordConfirmationInputLayout.id, "confirm_password"),
                input = passwordConfirmation.textChanges().map { it.toString() },
                validators = passwordValidators + listOf(isEqualsValidator),
                validationTriggers = listOf(passwordChanges.map { Unit }))


        val checkChanges = acceptCheck.checkedChanges()
        val acceptedField = RxField(key = Key(checkInputLayout.id, "accepted_terms"),
                input = checkChanges,
                validators = listOf(requireTrueValidator))

        rxForm = RxForm.Builder<Key>(submit.clicks(), strategy = ValidationStrategy.ON_SUBMIT)
                .addField(ageField)
                .addField(passwordField)
                .addField(confirmPasswordField)
                .addField(acceptedField)
                .build()

        disposables.add(rxForm.onFieldValidationChange()
                .subscribe { pair ->
                    findViewById<TextInputLayout>(pair.first.id)
                            .error = pair.second.joinToString(", ") { it.message }
                })
        disposables.add(rxForm.onFormValidationChange()
                .subscribe {
                    submit.isEnabled = it
                })

        disposables.add(rxForm.onValidSubmit()
                .map { data ->
                    data.map {
                        // removes the Int id that identified the fields in the screen
                        Pair(it.first.serverKey, it.second)
                    }
                }
                .map { it.toMap() }
                .subscribe {

                    //TODO: submit to server
                    Log.d("Debug", it.toString())

                    Toast.makeText(this@RegistrationActivity, it.toString() + " submitted to server", Toast.LENGTH_LONG).show()

                })


    }

    override fun onDestroy() {
        super.onDestroy()
        rxForm.dispose()
        disposables.dispose()
    }


    companion object {
        val PASSWORD_CONFIRMATION_MISMATCH = object : ValidationType {}
        val TERMS_ACCEPTANCE = object : ValidationType {}
    }
}
