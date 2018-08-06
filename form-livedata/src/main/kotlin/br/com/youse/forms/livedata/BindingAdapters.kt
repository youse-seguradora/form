package br.com.youse.forms.livedata

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import br.com.youse.forms.validators.ValidationMessage

@Suppress("UNUSED")
class BindingAdapters {

    companion object {

        @BindingAdapter(value = ["onFieldValidationChange"], requireAll = true)
        @JvmStatic
        fun onFieldValidationChange(view: TextInputLayout,
                                    validations: List<ValidationMessage>?) {
            view.error = validations?.firstOrNull()?.message

        }

        @BindingAdapter(value = ["formSubmit"])
        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun setFormSubmit(view: View, b: Boolean?) {
            // NOTE: Do nothing... we should be using Unit, but Databinding does not accept that
        }

        @InverseBindingAdapter(attribute = "formSubmit", event = "formSubmitAttrChanged")
        @JvmStatic
        @Suppress("UNUSED_PARAMETER")
        fun getFormSubmit(view: View): Boolean {
            // NOTE: Do nothing... we should be using Unit, but Databinding does not accept that
            return true
        }

        @BindingAdapter(value = ["formSubmitAttrChanged"])
        @JvmStatic
        fun setFormSubmitListener(view: View, listener: InverseBindingListener?) {
            if (listener == null) {
                return
            }
            view.setOnClickListener {
                listener.onChange()
            }
        }

        @BindingAdapter(value = ["formField"])
        @JvmStatic
        fun setFormField(view: EditText, newText: String?) {

            val oldText = view.text.toString()
            if (newText != oldText) {
                view.setText(newText)
            }
        }

        @InverseBindingAdapter(attribute = "formField", event = "formFieldAttrChanged")
        @JvmStatic
        fun getFormField(editText: EditText): String {
            return editText.text.toString()
        }

        @BindingAdapter(value = ["formFieldAttrChanged"])
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