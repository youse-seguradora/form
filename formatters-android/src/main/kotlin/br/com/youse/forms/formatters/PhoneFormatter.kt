package br.com.youse.forms.formatters

import android.content.res.Resources
import android.os.Build
import android.telephony.PhoneNumberUtils
import java.util.*


class PhoneFormatter(private val locale: Locale = getDeviceLocale()) : Formatter<String> {

    override fun format(input: String): String {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PhoneNumberUtils.formatNumber(input, locale.country) ?: input
        } else {
            PhoneNumberUtils.formatNumber(input) ?: input
        }
    }

    companion object {
        private fun getDeviceLocale(): Locale {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Resources.getSystem().configuration.locales[0]
            } else {
                Resources.getSystem().configuration.locale
            }
        }
    }
}