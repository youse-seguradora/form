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
package br.com.youse.forms.samples.registration

import android.content.Context
import android.util.AttributeSet
import br.com.youse.forms.extensions.isDigitsOnly
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class AgeEditText @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = com.google.android.material.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {
    // we don't really need a custom EditText for Age in this sample,
    // however I did one to make you think that a change may come from more than one place,
    // so it is good to centralize your listeners/observables as close as possible the thing being changed

    val onEnabledChange by lazy { PublishSubject.create<Boolean>() }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        // Nor android or Jake Wharthon (RxBinding) provides a callback or
        // Observable form this call, so we created one.
        onEnabledChange.onNext(enabled)

    }

    fun ageChanges(): Observable<Int> {
        return textChanges()
                // We convert the inputted CharSequence to String
                // and then to a valid representation of a Int
                // because we need to validate it as Int.
                .map {
                    it.toString()
                }
                .map {
                    if (it.isDigitsOnly())
                        it
                    else
                        "-1"
                }
                .map {
                    it.toInt()
                }
    }

}