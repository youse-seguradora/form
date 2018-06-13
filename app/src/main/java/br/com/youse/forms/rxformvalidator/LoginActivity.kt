package br.com.youse.forms.rxformvalidator

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.rxform.RxForm
import br.com.youse.forms.validators.MinLengthValidator
import br.com.youse.forms.validators.RequiredValidator
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class LoginActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val submitHappens = submit.clicks().share()
        val emailChanges = email.textChanges().share()
        val passwordChanges = password.textChanges().share()

        val form = RxForm.Builder<Int>(submitHappens)
                .addFieldValidations(emailContainer.id,
                        emailChanges, emailValidations)
                .addFieldValidations(passwordContainer.id,
                        passwordChanges,
                        passwordValidations)
                .build()

        disposables.add(form.onFieldValidationChange()
                .subscribe {
                    val field = findViewById<TextInputLayout>(it.first)
                    field.isErrorEnabled = it.second.isNotEmpty()
                    field.error = it.second.joinToString { it.message }
                })

        val onFormValidationChange = form.onFormValidationChange().share()
        disposables.add(onFormValidationChange
                .subscribe {
                    submit.isEnabled = it
                })

        disposables.add(form.onValidSubmit()
                //.map { it.map { it.second.toString() } }
                .subscribe { list ->
                    println(list)

                    val email = list.first { it.first == emailContainer.id }.second.toString()
                    val password = list.first { it.first == passwordContainer.id }.second.toString()

                    //TODO: submit email and password to server
                })
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}
