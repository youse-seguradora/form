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
package br.com.youse.forms.form

import br.com.youse.forms.form.models.ObservableValue
import kotlin.test.Test
import kotlin.test.assertEquals

class TestChangeObserver<T>(private val observableValue: ObservableValue<T>) : IObservableChange.ChangeObserver {
    val changes = mutableListOf<T>()
    override fun onChange() {
        changes.add(observableValue.value!!)
    }

    fun assertChange(index: Int, change: T): TestChangeObserver<T> {
        assertEquals(changes[index], change)
        return this
    }

    fun assertSize(size: Int): TestChangeObserver<T> {
        assertEquals(changes.size, size)
        return this
    }
}

class ObservableValueTest {


    @Test
    fun shouldUpdateValue() {
        val observableValue = ObservableValue(1)

        assertEquals(observableValue.value, 1)
        observableValue.value = 2
        assertEquals(observableValue.value, 2)

        val changeObserver = TestChangeObserver(observableValue)

        observableValue.addChangeListener(changeObserver)

        changeObserver.assertSize(1)
                .assertChange(0, 2)


        observableValue.value = 3
        changeObserver.assertSize(2)
                .assertChange(1, 3)

        // should not call onFormValidationChange...
        observableValue.value = 3
        changeObserver.assertSize(2)
                .assertChange(1, 3)

    }
}