package br.com.youse.forms.validators

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class RequiredValidatorTest {

    private lateinit var validator: Validator<CharSequence>
    @Before
    fun setup() {
        validator = RequiredValidator("should not be empty")
    }

    @Test
    fun shouldValidate() {
        Assert.assertFalse(validator.isValid(""))
        Assert.assertTrue(validator.isValid(" "))
        Assert.assertTrue(validator.isValid("1"))
    }
}