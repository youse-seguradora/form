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
import android.widget.CheckBox

@Suppress("UNUSED")
class CheckBoxBindingAdapters {

    companion object {

        /**
         *  Use this two way databinding to feed the form with CheckBoxes check changes.
         */
        @BindingAdapter(value = ["fieldCheck"])
        @JvmStatic
        fun setCheckBoxFormField(view: CheckBox, newValue: Boolean?) {
            val oldValue = view.isChecked
            if (oldValue != newValue) {
                view.isChecked = newValue ?: oldValue
            }
        }

        @InverseBindingAdapter(attribute = "fieldCheck", event = "fieldCheckAttrChanged")
        @JvmStatic
        fun getCheckBoxFormField(view: CheckBox): Boolean {
            return view.isChecked
        }

        @BindingAdapter(value = ["fieldCheckAttrChanged"])
        @JvmStatic
        fun setCheckBoxFormFieldListener(view: CheckBox, listener: InverseBindingListener?) {
            if (listener == null) {
                return
            }
            view.setOnCheckedChangeListener { _, _ ->
                listener.onChange()
            }
            listener.onChange()
        }
    }
}