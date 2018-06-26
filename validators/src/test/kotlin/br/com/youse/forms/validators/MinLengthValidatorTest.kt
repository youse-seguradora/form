package br.com.youse.forms.validators

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MinLengthValidatorTest {
    lateinit var validator: Validator<CharSequence>
    @Before
    fun setup() {
        validator = MinLengthValidator("invalid min length", 3)
    }

    @Test
    fun shouldValidate() {
        Assert.assertFalse(validator.isValid(""))
        Assert.assertFalse(validator.isValid("1"))
        Assert.assertFalse(validator.isValid("12"))

        Assert.assertFalse(validator.isValid(" "))
        Assert.assertTrue(validator.isValid("123"))
        Assert.assertTrue(validator.isValid("1234"))
    }
}