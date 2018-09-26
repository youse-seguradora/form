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
package br.com.youse.forms.samples.rx

import android.os.Bundle
import android.support.design.widget.TextInputLayout

import android.widget.Toast
import br.com.youse.forms.form.Form
import br.com.youse.forms.form.IForm
import br.com.youse.forms.rxform.IRxForm
import android.support.v7.app.AppCompatActivity
import br.com.youse.forms.R
import br.com.youse.forms.rxform.RxForm
import br.com.youse.forms.rxform.RxForm2
import br.com.youse.forms.validators.MinLengthValidator
import br.com.youse.forms.validators.RequiredValidator
import br.com.youse.forms.validators.ValidationMessage
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class RxLoginActivity : AppCompatActivity() {

    val MIN_PASSSWORD_LENGTH = 8
    val disposables = CompositeDisposable()
    val emailValidations by lazy {
        listOf(RequiredValidator(
                getString(R.string.empty_email_validation_message)
        ))
    }
    val passwordValidations by lazy {
        listOf(MinLengthValidator(
                getString(R.string.min_password_length_validation_message, MIN_PASSSWORD_LENGTH),
                MIN_PASSSWORD_LENGTH))
    }

    private lateinit var form: IRxForm<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val submitHappens = submit.clicks()
        val emailChanges = email.textChanges().map { it.toString() }
        val passwordChanges = password.textChanges().map { it.toString() }

        form = RxForm2.Builder<Int>(submitHappens)
                .addFieldValidations(emailContainer.id, emailChanges, emailValidations)
                .addFieldValidations(passwordContainer.id, passwordChanges, passwordValidations)
                .build()

        disposables.add(form.onFieldValidationChange()
                .subscribe {
                    val field = findViewById<TextInputLayout>(it.first)
                    field.isErrorEnabled = it.second.isNotEmpty()
                    field.error = it.second.joinToString { it.message }
                })

        disposables.add(form.onFormValidationChange()
                .subscribe {
                    submit.isEnabled = it
                })

        disposables.add(form.onValidSubmit()
                .subscribe { fields ->
                    println(fields)

                    val email = fields.first { it.first == emailContainer.id }.second.toString()
                    val password = fields.first { it.first == passwordContainer.id }.second.toString()

                    //TODO: submit email and password to server
                    Toast.makeText(this@RxLoginActivity, "$email and $password submitted to server", Toast.LENGTH_LONG).show()

                })
    }

    override fun onDestroy() {
        form.dispose()
        disposables.clear()
        super.onDestroy()
    }
}
