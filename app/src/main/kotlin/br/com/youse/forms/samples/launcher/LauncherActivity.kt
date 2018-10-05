package br.com.youse.forms.samples.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import br.com.youse.forms.R
import br.com.youse.forms.form.FieldValidationChange
import br.com.youse.forms.form.Form
import br.com.youse.forms.form.IForm
import br.com.youse.forms.form.models.FormField
import br.com.youse.forms.samples.form.FormLoginActivity
import br.com.youse.forms.samples.livedata.LiveDataLoginActivity
import br.com.youse.forms.samples.registration.RegistrationActivity
import br.com.youse.forms.samples.rx.RxLoginActivity
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.ValidationStrategy
import br.com.youse.forms.validators.ValidationType
import br.com.youse.forms.validators.Validator
import kotlinx.android.synthetic.main.activity_launcher.*

class LauncherActivity : Activity() {
    companion object {
        val RADIO_GROUP_VALIDATION_TYPE: ValidationType = object : ValidationType {}
    }

    val radioGroupValidator = object : Validator<Int> {
        override fun validationMessage(): ValidationMessage {
            return ValidationMessage("Selecione uma estrategia", validationType = RADIO_GROUP_VALIDATION_TYPE)
        }

        override fun isValid(input: Int?): Boolean {
            return input != null && input != 0 && input != -1
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val strategyField = FormField(key = strategyGroupInputLayout.id,
                errors = object : FieldValidationChange {
                    override fun onFieldValidationChange(validations: List<ValidationMessage>) {
                        strategyGroupInputLayout.error = validations.firstOrNull()?.message
                    }
                },
                validators = listOf(radioGroupValidator))

        val formsField = FormField(key = formGroupInputLayout.id,
                errors = object : FieldValidationChange {
                    override fun onFieldValidationChange(validations: List<ValidationMessage>) {
                        formGroupInputLayout.error = validations.firstOrNull()?.message
                    }
                },
                validators = listOf(radioGroupValidator))

        val form = Form.Builder<Int>(strategy = ValidationStrategy.ALL_TIME)
                .addField(formsField)
                .addField(strategyField)
                .setValidSubmitListener(object : IForm.ValidSubmit<Int> {
                    override fun onValidSubmit(fields: List<Pair<Int, Any?>>) {
                        // we can get the checked id from the view
                        val strategyCheckedId = strategyGroup.checkedRadioButtonId
                        // or from the field
                        val formCheckedId = formsField.input.value

                        val strategy = when (strategyCheckedId) {
                            afterSubmit.id -> ValidationStrategy.AFTER_SUBMIT
                            allTime.id -> ValidationStrategy.ALL_TIME
                            else -> null
                        }

                        val clazz: Class<out Activity>? = when (formCheckedId) {
                            formRadio.id -> FormLoginActivity::class.java
                            rxFormRadio.id -> RxLoginActivity::class.java
                            liveFormRadio.id -> LiveDataLoginActivity::class.java
                            else -> null
                        }

                        if (strategy == null || clazz == null) {
                            throw NullPointerException("Missing mapping for strategy or clazz")
                        }

                        val intent = Intent(this@LauncherActivity, clazz)
                        intent.putExtra("ValidationStrategy", strategy.name)
                        startActivity(intent)

                    }
                })
                .setFormValidationListener(object : IForm.FormValidationChange {
                    override fun onFormValidationChange(isValid: Boolean) {
                        submitButton.isEnabled = isValid
                    }
                }).build()

        // we need to set a initial value for each field
        strategyField.input.value = -1
        formsField.input.value = -1

        // and configure some way that allows the field input to be updated...
        // it can be text changes, focus changes, check changes, visibility changes...
        strategyGroup.setOnCheckedChangeListener { _, checkedId ->
            strategyField.input.value = checkedId
        }

        formGroup.setOnCheckedChangeListener { _, checkedId ->
            formsField.input.value = checkedId
        }

        submitButton.setOnClickListener {
            form.doSubmit()
        }


        registrationButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }


}
