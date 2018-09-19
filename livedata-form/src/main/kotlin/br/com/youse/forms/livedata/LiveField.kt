package br.com.youse.forms.livedata

import android.arch.lifecycle.MutableLiveData
import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.Validator

class LiveField<T, R>(val key: T,
                      val input: MutableLiveData<R> = MutableLiveData(),
                      val errors: MutableLiveData<List<ValidationMessage>> = MutableLiveData(),
                      val validators: List<Validator<R>>)