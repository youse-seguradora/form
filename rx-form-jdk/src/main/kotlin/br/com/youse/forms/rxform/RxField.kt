package br.com.youse.forms.rxform

import br.com.youse.forms.validators.Validator
import io.reactivex.Observable

class RxField<T, R>(val key: T,
                    val input: Observable<R>,
                    val validators: List<Validator<R>>,
                    val validationTriggers: List<Observable<Unit>> = emptyList())
