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
import android.view.View

@Suppress("UNUSED")
class ViewBindingAdapters {

    companion object {

        /**
         *  Use this two way databinding to feed the form with View focus changes.
         */

        @BindingAdapter(value = ["fieldFocus"])
        @JvmStatic
        fun setViewFocusChangeFormField(view: View, newValue: Boolean?) {
            val oldValue = view.hasFocus()
            if (oldValue != newValue) {
                if (newValue == true) {
                    view.requestFocus()
                } else {
                    view.clearFocus()
                }
            }
        }

        @InverseBindingAdapter(attribute = "fieldFocus", event = "fieldFocusAttrChanged")
        @JvmStatic
        fun getViewFocusChangeFormField(view: View): Boolean {
            return view.hasFocus()
        }

        @BindingAdapter(value = ["fieldFocusAttrChanged"])
        @JvmStatic
        fun setViewFocusChangeFormFieldListener(view: View, listener: InverseBindingListener?) {
            if (listener == null) {
                return
            }
            view.setOnFocusChangeListener { _, _ ->
                listener.onChange()
            }

            listener.onChange()
        }
    }
}