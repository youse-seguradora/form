package br.com.youse.forms.samples.livedata

import android.databinding.BindingAdapter
import android.view.View

object BindingAdapters {
    @BindingAdapter("visible")
    @JvmStatic fun setVisible(view: View, visible: Boolean) {
        view.visibility =  if (visible) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}