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
package br.com.youse.forms.form.models

import br.com.youse.forms.form.IObservableValue
import br.com.youse.forms.form.IObservableValue.ValueObserver

/**
 * Class that notifies its listener every time the value changes.
 * This class uses a nullable {@code value}, if an initial value is available
 * when the form is being build, use {@Link ObservableValue}.
 */
class DeferredObservableValue<T> : ObservableValidation(), IObservableValue<T> {
    private var listener: ValueObserver<T>? = null
    private var value: T? = null

    private var hasChanged = false

    /**
     * Sets a listener for {@code value} changes.
     */
    override fun setValueListener(valueObserver: ValueObserver<T>) {
        this.listener = valueObserver

        if (hasChanged) {
            valueObserver.onChange(value)
            onValidate()
        }
    }

    /**
     *  Updates the current value if the new value is different.
     *  Only calls the listener if the current value is updated.
     */
    fun setValue(value: T?) {
        if (value != this.value) {
            this.value = value
            this.hasChanged = true
            this.listener?.onChange(value)
        }
    }
}