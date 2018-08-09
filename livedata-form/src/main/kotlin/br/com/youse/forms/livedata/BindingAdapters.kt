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

package br.com.youse.forms.livedata

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView

@Suppress("UNUSED")
class BindingAdapters {

    companion object {

        @BindingAdapter(value = ["submit"])
        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun setFormSubmit(view: View, b: Boolean?) {
            // NOTE: Do nothing... we should be using Unit, but DataBinding does not accept that
            // Waiting release https://issuetracker.google.com/issues/78662035
        }

        @InverseBindingAdapter(attribute = "submit", event = "submitAttrChanged")
        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun getFormSubmit(view: View): Boolean? {
            // NOTE: Do nothing... we should be using Unit, but DataBinding does not accept that
            // Waiting release https://issuetracker.google.com/issues/78662035
            return null
        }

        @BindingAdapter(value = ["submitAttrChanged"])
        @JvmStatic
        fun setFormSubmitListener(view: View, listener: InverseBindingListener?) {
            if (listener == null) {
                return
            }
            view.setOnClickListener {
                listener.onChange()
            }
        }

        @BindingAdapter(value = ["field"])
        @JvmStatic
        fun setFormField(view: TextView, newText: CharSequence?) {
            val oldText = view.text
            // ATTENTION: We are using toString() because the equals and hashcode results are undefined for CharSequence
            // https://stackoverflow.com/questions/1049228/charsequence-vs-string-in-java#comment11633702_1049244
            if (oldText.toString() != newText.toString()) {
                view.text = newText
            }
        }

        @InverseBindingAdapter(attribute = "field", event = "fieldAttrChanged")
        @JvmStatic
        fun getFormField(view: TextView): CharSequence {
            return view.text
        }

        @BindingAdapter(value = ["fieldAttrChanged"])
        @JvmStatic
        fun setFormFieldListener(view: TextView, listener: InverseBindingListener?) {
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

}