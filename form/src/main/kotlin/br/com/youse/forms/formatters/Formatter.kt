package br.com.youse.forms.formatters

interface Formatter<T> {
    fun format(input: T): T
}