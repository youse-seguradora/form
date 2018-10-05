package br.com.youse.forms.rxform

import br.com.youse.forms.validators.ValidationMessage
import br.com.youse.forms.validators.Validator
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class RxField<T, R>(val key: T,
                    val input: Observable<R> = BehaviorSubject.create(),
                    val errors: BehaviorSubject<List<ValidationMessage>> = BehaviorSubject.create(),
                    val validators: List<Validator<R>> = emptyList(),
                    val validationTriggers: List<Observable<Unit>> = emptyList())
