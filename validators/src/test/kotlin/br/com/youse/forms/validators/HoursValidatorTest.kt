package br.com.youse.forms.validators

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HoursValidatorTest {

    private lateinit var validator: Validator<String>
    @Before
    fun setup() {
        validator = HoursValidator("invalid message", ":")
    }

    @Test
    fun shouldValidate() {

        Assert.assertFalse(validator.isValid(""))
        Assert.assertFalse(validator.isValid(" "))
        Assert.assertFalse(validator.isValid("a"))
        Assert.assertFalse(validator.isValid("0"))
        Assert.assertFalse(validator.isValid("01"))
        Assert.assertFalse(validator.isValid("01:"))
        Assert.assertFalse(validator.isValid(":0"))
        Assert.assertFalse(validator.isValid("24:0"))
        Assert.assertFalse(validator.isValid("24:0"))

        Assert.assertFalse(validator.isValid("24:0:00"))

        Assert.assertTrue(validator.isValid("0:0"))
        Assert.assertTrue(validator.isValid("23:59"))
        Assert.assertTrue(validator.isValid("00:0"))
    }
}