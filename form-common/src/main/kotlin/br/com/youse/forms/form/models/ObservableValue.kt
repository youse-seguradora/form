package br.com.youse.forms.form.models


import br.com.youse.forms.form.IObservableValue
import br.com.youse.forms.form.IObservableValue.ValueObserver
import kotlin.properties.Delegates

/**
 * Class that notifies its listener every time the value changes.
 * This class requires an {@code initialValue}, if one is not available, use {@Link DeferredObservableValue} class.
 */
class ObservableValue<T>(initialValue: T) : IObservableValue<T> {

    private var listener: ValueObserver<T>? = null

    var value: T by Delegates.observable(initialValue) { _, old, new ->
        if (old != new) {
            listener?.onChange(new)
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
    
