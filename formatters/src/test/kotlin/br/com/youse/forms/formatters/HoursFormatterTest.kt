package br.com.youse.forms.formatters

import org.junit.Assert
import org.junit.Before
import org.junit.Test

class HoursFormatterTest {

    private lateinit var formatter: TextFormatter

    @Before
    fun setup() {
        formatter = HoursFormatter(":")
    }

    @Test
    fun shouldFormat() {
        Assert.assertEquals(formatter.format(""), "")
        Assert.assertEquals(formatter.format("1"), "1")
        Assert.assertEquals(formatter.format("12"), "12:")
        Assert.assertEquals(formatter.format("123"), "12:3")
        Assert.assertEquals(formatter.format("12:3"), "12:3")
        Assert.assertEquals(formatter.format("1234"), "12:34")
        Assert.assertEquals(formatter.format("12345"), "12:34")
    }
}