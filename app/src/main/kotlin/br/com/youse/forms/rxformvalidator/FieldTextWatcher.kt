package br.com.youse.forms.rxformvalidator

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import br.com.youse.forms.form.IForm

class FieldTextWatcher(private val field: IForm.ObservableValue<CharSequence>) : TextWatcher {


    override fun afterTextChanged(s: Editable) {
        field.value = s
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }
}

fun TextView.fieldChanges(): IForm.ObservableValue<CharSequence> {
    val field = IForm.ObservableValue(text)
    addTextChangedListener(FieldTextWatcher(field))
    return field
}
