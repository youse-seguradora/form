package br.com.youse.forms.livedata

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestObserver<T>(private val ld: MutableLiveData<T>) : Observer<T> {
    private val changes = mutableListOf<T?>()
    override fun onChanged(t: T?) {
        changes.add(t)
    }

    fun observe(): TestObserver<T> {
        ld.observeForever(this)
        return this
    }

    fun dispose(): TestObserver<T> {
        ld.removeObserver(this)
        return this
    }

    fun assertNoValues(): TestObserver<T> {
        assertEquals(changes.size, 0)
        return this
    }

    fun assertAnyValue(): TestObserver<T> {
        assertTrue(changes.size > 0)
        return this
    }

    fun assertValue(t: T): TestObserver<T> {
        assertEquals(changes.last(), t)
        return this
    }

    fun assertSize(size: Int): TestObserver<T> {
        assertEquals(changes.size, size)
        return this
    }
}