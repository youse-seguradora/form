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
package br.com.youse.forms.form.models

import br.com.youse.forms.form.IObservableChange
import br.com.youse.forms.form.IObservableValue
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.Validator

open class FormField<T, R> constructor(val key: T,
                                  val input: IObservableValue<R> = ObservableValue(),
                                  val errors: IObservableValue<List<ValidationMessage>> = ObservableValue(),
                                  val enabled: IObservableValue<Boolean> = ObservableValue(true),
                                  val validators: List<Validator<R>> = emptyList(),
                                  val validationTriggers: List<IObservableChange> = emptyList()) {

    constructor(key: T,
                initialValue: R,
                errors: IObservableValue<List<ValidationMessage>> = ObservableValue(),
                enabled: IObservableValue<Boolean> = ObservableValue(true),
                validators: List<Validator<R>> = emptyList(),
                validationTriggers: List<IObservableChange> = emptyList()) : this(
            key = key,
            input = ObservableValue(initialValue),
            errors = errors,
            enabled = enabled,
            validators = validators,
            validationTriggers = validationTriggers)

    internal fun hasErrors(): Boolean {
        return errors.value?.isNotEmpty() ?: false
    }

    internal fun cleanErrors() {
        errors.value = emptyList()
    }

    internal fun validate() {
        val value = input.value

        val messages = mutableListOf<ValidationMessage>()

        for (validator in validators) {
            if (!validator.isValid(value)) {
                messages += validator.validationMessage()
            }
        }

        errors.value = messages
    }
}