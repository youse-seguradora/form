package br.com.youse.forms.samples.livedata

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer

class LiveEvent<T>(private val content: T) {
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

class MutableLiveEvent<T> : MutableLiveData<LiveEvent<T>>() {

    fun setEvent(event: T) {
        value = LiveEvent(event)
    }

    fun postEvent(event: T) {
        postValue(LiveEvent(event))
    }
}

abstract class LiveEventObserver<T> : Observer<LiveEvent<T>> {
    override fun onChanged(t: LiveEvent<T>?) {
        onEventChanged(t?.getContent())
    }

    abstract fun onEventChanged(event: T?)

}