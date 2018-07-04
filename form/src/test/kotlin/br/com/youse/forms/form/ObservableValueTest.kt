package br.com.youse.forms.form


import org.junit.Assert.*
import org.junit.Test

class ObservableValueTest {


    @Test
    fun shouldUpdateValue() {
        val observableValue = IForm.ObservableValue(1)

        assertEquals(observableValue.value, 1)
        observableValue.value = 2
        assertEquals(observableValue.value, 2)
        var expectedValue = 2
        var count = 0
        observableValue.setValueListener(object : IForm.ObservableValue.ValueObserver<Int> {
            override fun onChange(value: Int) {
                assertEquals(value, expectedValue)
                count++
            }
        })
        assertEquals(count, 1)
        expectedValue = 3

        observableValue.value = 3
        assertEquals(count, 2)

        // should not call onChange...
        observableValue.value = 3
        assertEquals(count, 2)
    }
}