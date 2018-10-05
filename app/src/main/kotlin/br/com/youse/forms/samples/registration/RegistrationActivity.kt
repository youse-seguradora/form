package br.com.youse.forms.samples.registration

import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import br.com.youse.forms.R
import br.com.youse.forms.extensions.isDigitsOnly
import br.com.youse.forms.rxform.IRxForm
import br.com.youse.forms.rxform.RxField
import br.com.youse.forms.rxform.RxForm
import br.com.youse.forms.validators.*
import com.jakewharton.rxbinding2.view.clicks
import com.jakewharton.rxbinding2.widget.checkedChanges
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    lateinit var rxForm: IRxForm<Int>

    private val disposables = CompositeDisposable()
    private val passswordValidators = listOf(
            MinLengthValidator("the password should be at least 8 letters long", 8)
    )

    private val ageValidators = listOf(
            MinValueValidator("you should be at least 21 old to register", 21)
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

        val ageChanges = age.textChanges()
                // We convert the inputted CharSequence to String and then to Int
                // because we need to validate it as Int.
                .map {
                    it.toString()
                }
                .map { if (it.isBlank()) "-1" else it }
                .map {
                    if (it.isDigitsOnly())
                        it.toInt()
                    else
                        -1
                }

        val checkChanges = acceptCheck.checkedChanges()

        val ageField = RxField(key = ageInputLayout.id,
                input = ageChanges,
                validators = ageValidators)

        val passwordChanges = password.textChanges()
                .map { it.toString() }
                .share()

        val passwordField = RxField(key = passwordInputLayout.id,
                input = passwordChanges,
                validators = passswordValidators)


        val confirmPasswordField = RxField(key = passwordConfirmationInputLayout.id,
                input = passwordConfirmation.textChanges().map { it.toString() },
                validators = passswordValidators + listOf(isEqualsValidator),
                validationTriggers = listOf(passwordChanges.map { Unit }))


        val acceptedField = RxField(key = checkInputLayout.id,
                input = checkChanges,
                validators = listOf(requireTrueValidator))

        rxForm = RxForm.Builder<Int>(submit.clicks())
                .addField(ageField)
                .addField(passwordField)
                .addField(confirmPasswordField)
                .addField(acceptedField)
                .build()

        disposables.add(rxForm.onFieldValidationChange()
                .subscribe {
                    findViewById<TextInputLayout>(it.first)
                            .error = it.second.joinToString(", ") { it.message }
                })
        disposables.add(rxForm.onFormValidationChange()
                .subscribe {
                    submit.isEnabled = it
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
