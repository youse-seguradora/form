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
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val form = RxForm.Builder<Int>(submit.clicks(), ValidationStrategy.AFTER_SUBMIT)
                .addFieldValidations(emailContainer.id,
                        email.textChanges(),
                        listOf(RequiredValidator("Email não pode ficar em branco")))
                .addFieldValidations(passwordContainer.id,
                        password.textChanges(),
                        listOf(MinLengthValidator("Tamanho minimo de 8 cars", 8)))
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
    }

    override fun onDestroy() {
        disposables.clear()
        super.onDestroy()
    }
}
