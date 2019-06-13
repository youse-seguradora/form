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

package br.com.youse.forms.livedata.databinding

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener

@Suppress("UNUSED")
object EditTextBindingAdapters {

    /**
     *  Use this two way databinding to feed the form with EditTexts text changes.
     */
    @BindingAdapter(value = ["fieldText"])
    @JvmStatic
    fun setEditTextFormField(view: EditText, newValue: String?) {
        val oldValue = view.text.toString()
        if (oldValue != newValue) {
            view.setText(newValue)
        }
    }

    @InverseBindingAdapter(attribute = "fieldText", event = "fieldTextAttrChanged")
    @JvmStatic
    fun getEditTextFormField(view: EditText): String {
        return view.text.toString()
    }

    @BindingAdapter(value = ["fieldTextAttrChanged"])
    @JvmStatic
    fun setEditTextFormFieldListener(view: EditText, listener: InverseBindingListener?) {
        if (listener == null) {
            return
        }
        view.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                listener.onChange()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        listener.onChange()
    }
}