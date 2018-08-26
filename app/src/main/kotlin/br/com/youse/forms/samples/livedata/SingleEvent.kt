package br.com.youse.forms.samples.livedata

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

class SingleEvent<T>(private val content: T) {
    private var consumed = false	
     fun getContent(): T? {	
        return if (!consumed) {	
            consumed = true	
            content	
        } else {	
            null	
        }	
    }	
}

class SingleLiveEvent<T> : MutableLiveData<SingleEvent<T>>() {

    fun setEvent(event: T) {
        value = SingleEvent(event)
    }

    fun postEvent(event: T) {
        postValue(SingleEvent(event))
    }
}

abstract class SingleEventObserver<T> : Observer<SingleEvent<T>> {
    override fun onChanged(t: SingleEvent<T>?) {
        onEventChanged(t?.getContent())
    }

    abstract fun onEventChanged(event: T?)

}