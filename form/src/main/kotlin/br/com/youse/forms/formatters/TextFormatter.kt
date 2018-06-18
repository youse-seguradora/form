package br.com.youse.forms.formatters

interface TextFormatter : Formatter<CharSequence> {
    /**
     * Returns which position in the output value the cursor should be positioned.
     */
    fun getCursorPosition(previous: CharSequence, input: CharSequence, output: CharSequence): Int
}