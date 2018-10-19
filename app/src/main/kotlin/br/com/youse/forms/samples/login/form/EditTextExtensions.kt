package br.com.youse.forms.samples.login.form

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import br.com.youse.forms.form.models.ObservableValue

fun EditText.addObservableValue(): ObservableValue<String> {
    val observableValue = ObservableValue(text.toString())

    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            observableValue.value = p0?.toString()
        }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })
    return observableValue
}
