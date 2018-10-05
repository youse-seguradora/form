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

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import br.com.youse.forms.validators.ValidationMessage

@Suppress("UNUSED")
class BindingAdapters {

    companion object {

        @BindingAdapter(value = ["field"])
        @JvmStatic
        fun setFormField(view: EditText, newText: String?) {
            val oldText = view.text.toString()
            if (oldText != newText) {
                view.setText(newText)
            }
        }

        @InverseBindingAdapter(attribute = "field", event = "fieldAttrChanged")
        @JvmStatic
        fun getFormField(view: EditText): String {
            return view.text.toString()
        }

        @BindingAdapter(value = ["fieldAttrChanged"])
        @JvmStatic
        fun setFormFieldListener(view: EditText, listener: InverseBindingListener?) {
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

        @BindingAdapter(value = ["fieldError"])
        @JvmStatic
        fun onFieldValidationChange(view: TextInputLayout,
                                    validations: List<ValidationMessage>?) {
            view.error = validations?.firstOrNull()?.message
        }

        @BindingAdapter(value = ["formEnabled"])
        @JvmStatic
        fun onFormValidationChange(view: View, enabled: Boolean?) {
            enabled?.let {
                view.isEnabled = enabled
            }
        }
    }

}