package br.com.youse.forms.formatters

interface TextFormatter : Formatter<String> {
    fun getCursorPosition(previous: String, input: String, output: String): Int
}