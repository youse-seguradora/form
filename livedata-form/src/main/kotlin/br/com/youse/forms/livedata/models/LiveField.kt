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
package br.com.youse.forms.livedata.models

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.Validator

class LiveField<T, R> constructor(val key: T,
                                  val input: MutableLiveData<R> = MutableLiveData(),
                                  val errors: MutableLiveData<List<ValidationMessage>> = MutableLiveData(),
                                  val enabled: LiveData<Boolean> = MutableLiveData(),
                                  val validators: List<Validator<R>>,
                                  val validationTriggers: List<LiveData<Unit>> = emptyList()) {

    constructor(key: T,
                initialValue: R,
                errors: MutableLiveData<List<ValidationMessage>> = MutableLiveData(),
                enabled: LiveData<Boolean> = MutableLiveData(),
                validators: List<Validator<R>>,
                validationTriggers: List<LiveData<Unit>> = emptyList()) : this(
            key = key,
            input = MutableLiveData<R>().apply { value = initialValue },
            errors = errors,
            enabled = enabled,
            validators = validators,
            validationTriggers = validationTriggers
    )
}