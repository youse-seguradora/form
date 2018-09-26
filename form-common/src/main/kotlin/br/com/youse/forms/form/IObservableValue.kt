package br.com.youse.forms.form

interface IObservableValue<T> {
    interface ValueObserver<T> {
        /**
         * Notifies a value change.
         */
        fun onChange(value: T)
    }

    /**
     * Sets a listener for {@code value} changes.s
     */
    fun setValueListener(valueObserver: IObservableValue.ValueObserver<T>)
}