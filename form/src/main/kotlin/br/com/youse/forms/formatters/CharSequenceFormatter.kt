package br.com.youse.forms.formatters

/**
 * Formats a CharSequence input value and returns which position in the
 * output value the cursor should be positioned.
 */

interface CharSequenceFormatter : Formatter<CharSequence> {
    fun getCursorPosition(previous: CharSequence, input: CharSequence, output: CharSequence): Int
}