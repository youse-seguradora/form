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
package br.com.youse.forms.samples.login.form

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import br.com.youse.forms.R
import br.com.youse.forms.form.Form
import br.com.youse.forms.form.IForm
import br.com.youse.forms.form.IForm.*
import br.com.youse.forms.form.models.FormField
import br.com.youse.forms.samples.launcher.name
import br.com.youse.forms.samples.launcher.valueOf
import br.com.youse.forms.validators.MinLengthValidator
import br.com.youse.forms.validators.RequiredValidator
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import kotlinx.android.synthetic.main.activity_main.*


class FormLoginActivity : AppCompatActivity(),
        IForm.FieldValidationChange<Int>,
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val param = intent.getStringExtra("ValidationStrategy")
                ?: ValidationStrategy.AFTER_SUBMIT.name()
        ValidationStrategy.AfterSubmit(false, true)
        val strategy = ValidationStrategy.valueOf(param)!!

        val emailChanges = email.addObservableValue()
        val passwordChanges = password.addObservableValue()


        val emailField = FormField(
                key = emailContainer.id,
                input = emailChanges,
                validators = emailValidations
        )

        val passwordField = FormField(
                key = passwordContainer.id,
                input = passwordChanges,
                validators = passwordValidations
        )

        val form = Form.Builder<Int>(strategy = strategy)
                .setFieldValidationListener(this)
                .setFormValidationListener(this)
                .setValidSubmitListener(this)
                .setSubmitFailedListener(this)
                .addField(emailField)
                .addField(passwordField)
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
