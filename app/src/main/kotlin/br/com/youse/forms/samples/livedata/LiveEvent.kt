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