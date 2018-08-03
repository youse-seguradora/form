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
package br.com.youse.forms.validators

/**
 * Interface to validate an input, each Validator should validate only one aspect of an input value
 * and return a proper validation message.
 */
interface Validator<in T> {
    /**
     *  Validates if the input is a valid value.
     *  Returns true if the input is valid, false otherwise.
     */
    fun isValid(input: T): Boolean

    /**
     * Returns a validation message, it is used only when isValid method returns false.
     */
    fun validationMessage(): ValidationMessage
}

/**
 * A ValidationType represented why validation failed.
 * Multiple Validators can have the same ValidationType.
 */
interface ValidationType

/**
 * Holds a validation failed message and the validation type.
 */
data class ValidationMessage(val message: String, val validationType: ValidationType)
