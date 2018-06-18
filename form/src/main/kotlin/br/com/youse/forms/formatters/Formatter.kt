package br.com.youse.forms.formatters

interface Formatter<T> {
    /**
     * Formats an input value and returns the formatted value.
     */
    fun format(input: T): T
}