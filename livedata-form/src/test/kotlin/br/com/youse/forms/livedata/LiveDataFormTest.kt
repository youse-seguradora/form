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
package br.com.youse.forms.livedata

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import br.com.youse.forms.livedata.models.LiveField
import br.com.youse.forms.validators.ValidationStrategy
import org.junit.Rule
import org.junit.rules.TestRule
import kotlin.test.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestObserver<T>(private val ld: MutableLiveData<T>) : Observer<T> {
    private val changes = mutableListOf<T?>()
    override fun onChanged(t: T?) {
        changes.add(t)
    }

    fun observe(): TestObserver<T> {
        ld.observeForever(this)
        return this
    }

    fun dispose(): TestObserver<T> {
        ld.removeObserver(this)
        return this
    }

    fun assertNoValues(): TestObserver<T> {
        assertEquals(changes.size, 0)
        return this
    }

    fun assertAnyValue(): TestObserver<T> {
        assertTrue(changes.size > 0)
        return this
    }

    fun assertValue(t: T): TestObserver<T> {
        assertEquals(changes.last(), t)
        return this
    }

    fun assertSize(size: Int): TestObserver<T> {
        assertEquals(changes.size, size)
        return this
    }
}

class LiveDataFormTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()


    private lateinit var email: LiveField<Int, String>
    private lateinit var password: LiveField<Int, String>
    private lateinit var age: LiveField<Int, Int>


    @BeforeTest
    fun setup() {
        email = LiveField(key = EMAIL_ID, validators = emailValidators)
        password = LiveField(key = PASSWORD_ID, validators = passwordValidators)
        age = LiveField(key = AGE_ID, validators = ageValidators)
    }

    @Test
    fun shouldValidateAfterSubmit() {
        validate(ValidationStrategy.AFTER_SUBMIT)
    }

    @Test
    fun shouldValidateAllTime() {
        validate(ValidationStrategy.ALL_TIME)
    }


    private fun validate(strategy: ValidationStrategy) {

        val isAfterSubmit = strategy == ValidationStrategy.AFTER_SUBMIT

        val form: ILiveDataForm<Int> = LiveDataForm.Builder<Int>(strategy = strategy)
                .addField(email)
                .addField(password)
                .addField(age)
                .build()

        val emailErrorsSub = TestObserver(email.errors).observe()

        val passwordErrorsSub = TestObserver(password.errors).observe()

        val ageErrorsSub = TestObserver(age.errors).observe()

        val failedSubmitSub = TestObserver(form.onSubmitFailed).observe()


        val formValidationSub = TestObserver(form.onFormValidationChange).observe()


        val validSubmitSub = TestObserver(form.onValidSubmit).observe()

        val fieldSubs = listOf(emailErrorsSub, passwordErrorsSub, ageErrorsSub)
        val allSubs = fieldSubs + listOf(failedSubmitSub, formValidationSub, validSubmitSub)

        allSubs.forEach { it.assertNoValues() }

        email.input.value = ""
        password.input.value = ""
        age.input.value = MIN_AGE_VALUE - 1

        if (isAfterSubmit) {
            // nothing should be initialized yet
            allSubs.forEach { it.assertNoValues() }
        } else {
            // validate fields all the time
            fieldSubs.forEach { it.assertSize(1) }

            // form state is invalid
            formValidationSub.assertValue(false)

            // did not call the submit related callbacks
            failedSubmitSub.assertNoValues()
            validSubmitSub.assertNoValues()
        }

        // doSubmit will not change the field validations size if
        // strategy ALL_TIME, but it will for AFTER_SUBMIT
        form.doSubmit()

        // field validation was triggered
        fieldSubs.forEach { it.assertSize(1) }

        // form state validation was triggered
        formValidationSub.assertAnyValue()

        // the submit caused an evaluation and the form submission failed
        failedSubmitSub.assertAnyValue()
        validSubmitSub.assertNoValues()

        // checking fields validation messages
        emailErrorsSub.assertValue(listOf(INVALID_EMAIL_MESSAGE))
        passwordErrorsSub.assertValue(listOf(INVALID_PASSWORD_MESSAGE))
        ageErrorsSub.assertValue(listOf(TOO_SMALL_MESSAGE))

        // the form is not valid
        formValidationSub.assertValue(false)

        // the failed submit messages
        failedSubmitSub.assertValue(listOf(
                Pair(EMAIL_ID, listOf(INVALID_EMAIL_MESSAGE)),
                Pair(PASSWORD_ID, listOf(INVALID_PASSWORD_MESSAGE)),
                Pair(AGE_ID, listOf(TOO_SMALL_MESSAGE))))

        // valid submit still not triggered
        validSubmitSub.assertNoValues()


        // valid form
        email.input.value = VALID_EMAIL
        password.input.value = VALID_PASSWORD
        age.input.value = MIN_AGE_VALUE

        // all messages are empty
        fieldSubs.forEach { it.assertValue(emptyList()) }

        // the form is valid
        formValidationSub.assertValue(true)

        allSubs.forEach { it.dispose() }
    }

}