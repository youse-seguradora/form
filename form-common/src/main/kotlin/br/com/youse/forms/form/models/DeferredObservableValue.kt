package br.com.youse.forms.form.models

import br.com.youse.forms.form.IObservableValue
import br.com.youse.forms.form.IObservableValue.ValueObserver

/**
 * Class that notifies its listener every time the value changes.
 * This class uses a nullable {@code value}, if an initial value is available
 * when the form is being build, use {@Link ObservableValue}.
 */
class DeferredObservableValue<T> : IObservableValue<T> {
    private var listener: ValueObserver<T>? = null
    private var value: T? = null

    /**
     * Sets a listener for {@code value} changes.
     */
    override fun setValueListener(valueObserver: ValueObserver<T>) {
        this.listener = valueObserver
        if (value != null) {
            valueObserver.onChange(value!!)
        }
    }

    /**
     *  Updates the current value if the new value is different.
     *  Only calls the listener if the current value is updated.
     */
    fun setValue(value: T) {
        if (value != this.value) {
            this.value = value
            this.listener?.onChange(value)
        }
    }
}