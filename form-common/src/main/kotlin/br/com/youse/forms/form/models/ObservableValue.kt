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

import br.com.youse.forms.form.IObservableChange
import br.com.youse.forms.form.IObservableValue

/**
 * Class that notifies its listeners every time the value changes.
 */
class ObservableValue<T> : ObservableChange, IObservableValue<T> {

    private var hasChanged = false

    /**
     *  Updates the current realValue if the newValue is different.
     *  Only calls the notifyChange() if the current value is updated.
     */
    private var realValue: T? = null

    override var value: T?
        set(newValue) {
            if (newValue != this.realValue) {
                realValue = newValue
                hasChanged = true
                notifyChange()
            }
        }
        get() {
            return realValue
        }

    constructor() : super()

    constructor(value: T) : super() {
        this.value = value
    }

    override fun addChangeListener(observer: IObservableChange.ChangeObserver) {
        super.addChangeListener(observer)
        if (hasChanged) {
            observer.onChange()
        }
    }

}