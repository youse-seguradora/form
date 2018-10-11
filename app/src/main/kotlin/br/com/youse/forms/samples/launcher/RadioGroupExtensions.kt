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
package br.com.youse.forms.samples.launcher

import android.support.design.widget.TextInputLayout
import android.widget.RadioGroup
import br.com.youse.forms.form.IObservableChange
import br.com.youse.forms.form.models.FormField
import br.com.youse.forms.form.models.ObservableValue
import br.com.youse.forms.validators.Validator


fun RadioGroup.checkChangesField(validators: List<Validator<Int>>): FormField<Int, Int> {

    val inputLayout = (parent as TextInputLayout)
    val observableValue = ObservableValue<Int>()

    setOnCheckedChangeListener { _, checkedId ->
        observableValue.value = checkedId
    }

    val formField = FormField(
            key = inputLayout.id,
            input = observableValue,
            validators = validators
    )

    formField.observeErrors(inputLayout)

    return formField
}


fun FormField<*, *>.observeErrors(inputLayout: TextInputLayout): IObservableChange.ChangeObserver {

    val observer = object : IObservableChange.ChangeObserver {
        override fun onChange() {
            val validations = errors.value ?: emptyList()
            inputLayout.error = validations.firstOrNull()?.message
        }
    }

    errors.addChangeListener(observer = observer)

    return observer
}