package br.com.youse.forms.rxform

import br.com.youse.forms.validators.ValidationStrategy
import io.reactivex.Observable

class RxForm2Test : RxFormTests() {
    override fun <T> getBuilder(submit: Observable<Unit>, strategy: ValidationStrategy): IRxForm.Builder<T> {
        return RxForm2.Builder(submit, strategy)
    }
}