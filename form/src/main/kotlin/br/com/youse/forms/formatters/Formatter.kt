package br.com.youse.forms.formatters

/**
 * Formats an input value and returns the formatted value
 */
interface Formatter<T> {
    fun format(input: T): T
}