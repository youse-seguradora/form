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
package br.com.youse.forms.samples.launcher

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import br.com.youse.forms.R
import br.com.youse.forms.form.Form
import br.com.youse.forms.form.IForm
import br.com.youse.forms.form.IForm.FormValidationChange
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
        private val RADIO_GROUP_VALIDATION_TYPE: ValidationType = object : ValidationType {}
    }

    private val radioGroupValidator = object : Validator<Int> {
        override fun validationMessage(): ValidationMessage {
            return ValidationMessage("Select an option.", validationType = RADIO_GROUP_VALIDATION_TYPE)
        }

        override fun isValid(input: Int?): Boolean {
            return input != null && input != 0 && input != -1
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        val strategyField = strategyGroup.checkChangesField(listOf(radioGroupValidator))
        val formsField = formGroup.checkChangesField(listOf(radioGroupValidator))

        val form = Form.Builder<Int>()
                .addField(formsField)
                .addField(strategyField)
                .setValidSubmitListener(onValidSubmitListener)
                .setFormValidationListener(onFormValidationListener)
                .build()

        submitButton.setOnClickListener {
            form.doSubmit()
        }

        registrationButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }


    private val onFormValidationListener = object : FormValidationChange {
        override fun onFormValidationChange(isValid: Boolean) {
            submitButton.isEnabled = isValid
        }
    }

    private val onValidSubmitListener = object : IForm.ValidSubmit<Int> {
        override fun onValidSubmit(fields: List<Pair<Int, Any?>>) {

            val strategy = getSelectedStrategy()
            val clazz = getActivityClass()

            if (strategy == null || clazz == null) {
                throw NullPointerException("Missing mapping for strategy or clazz")
            }

            val intent = Intent(this@LauncherActivity, clazz)
            intent.putExtra("ValidationStrategy", strategy.name)
            startActivity(intent)

        }
    }

    private fun getSelectedStrategy(): ValidationStrategy? {
        val strategyCheckedId = strategyGroup.checkedRadioButtonId

        return when (strategyCheckedId) {
            afterSubmit.id -> ValidationStrategy.AFTER_SUBMIT
            allTime.id -> ValidationStrategy.ALL_TIME
            else -> null
        }
    }

    private fun getActivityClass(): Class<out Activity>? {
        val formCheckedId = formGroup.checkedRadioButtonId
        return when (formCheckedId) {
            formRadio.id -> FormLoginActivity::class.java
            rxFormRadio.id -> RxLoginActivity::class.java
            liveFormRadio.id -> LiveDataLoginActivity::class.java
            else -> null
        }
    }

}
