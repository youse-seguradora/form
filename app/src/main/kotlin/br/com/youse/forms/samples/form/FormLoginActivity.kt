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
package br.com.youse.forms.samples.form

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import br.com.youse.forms.R
import br.com.youse.forms.form.Form
import br.com.youse.forms.form.IForm.*
import br.com.youse.forms.form.models.ObservableValue
import br.com.youse.forms.validators.MinLengthValidator
import br.com.youse.forms.validators.RequiredValidator
import br.com.youse.forms.validators.ValidationMessage
import kotlinx.android.synthetic.main.activity_main.*

class FormLoginActivity : AppCompatActivity(),
        FieldValidationChange<Int>,
        FormValidationChange,
        ValidSubmit<Int>,
        SubmitFailed<Int> {


    val MIN_PASSSWORD_LENGTH = 8

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

    private fun getTextWatcher(observableValue: ObservableValue<String>): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                observableValue.value = p0?.toString() ?: ""
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailChanges = ObservableValue(email.text.toString())
        val passwordChanges = ObservableValue(password.text.toString())

        email.addTextChangedListener(getTextWatcher(emailChanges))
        password.addTextChangedListener(getTextWatcher(passwordChanges))


        val form = Form.Builder<Int>()
                .setFieldValidationListener(this)
                .setFormValidationListener(this)
                .setValidSubmitListener(this)
                .setSubmitFailedListener(this)
                .addField(emailContainer.id,
                        emailChanges, emailValidations)
                .addField(passwordContainer.id,
                        passwordChanges,
                        passwordValidations)
                .build()

        submit.setOnClickListener {
            form.doSubmit()
        }

    }

    override fun onFieldValidationChange(key: Int, validations: List<ValidationMessage>) {
        val field = findViewById<TextInputLayout>(key)
        field.isErrorEnabled = validations.isNotEmpty()
        field.error = validations.joinToString { it.message }

    }

    override fun onFormValidationChange(isValid: Boolean) {
        submit.isEnabled = isValid
    }

    override fun onSubmitFailed(validations: List<Pair<Int, List<ValidationMessage>>>) {
        validations.firstOrNull()?.first?.let {
            // Scroll to this view to highlight the problem, or make the field blink
            // it's up to you. :-P
            findViewById<TextInputLayout>(it).error = """
                               Hey, look over here,
                               I am the first field with validations problem
                               """.trimIndent()
        }
    }

    override fun onValidSubmit(fields: List<Pair<Int, Any?>>) {
        val email = fields.first { it.first == emailContainer.id }.second.toString()
        val password = fields.first { it.first == passwordContainer.id }.second.toString()

        //TODO: submit email and password to server
        Log.d("Debug", "email : $email, password: $$password")

        Toast.makeText(this@FormLoginActivity, "$email and $password submitted to server", Toast.LENGTH_LONG).show()

    }

}
