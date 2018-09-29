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
import kotlin.properties.Delegates

/**
 * Class that notifies its listener every time the value changes.
 * This class requires an {@code initialValue}, if one is not available, use {@Link DeferredObservableValue} class.
 */
class ObservableValue<T>(initialValue: T) : ObservableValidation(), IObservableValue<T> {

    private var listener: ValueObserver<T>? = null

    var value: T by Delegates.observable(initialValue) { _, old, new ->
        if (old != new) {
            listener?.onChange(new)
            onValidate()
        }
    }

    /**
     * Sets a listener for {@code value} changes.
     */
    override fun setValueListener(valueObserver: ValueObserver<T>) {
        listener = valueObserver
        listener?.onChange(value)
    }
}
    
