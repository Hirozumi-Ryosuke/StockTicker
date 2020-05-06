package com.example.stockticker.ticker.mock

import kotlin.reflect.KClass

object Mocker {

    private val mocks = HashMap<KClass<*>, Any>()

    fun <T : Any> provide(clazz: KClass<T>): T {
        if (!mocks.containsKey(clazz)) {
            val mock = mock(clazz.java)
            mocks[clazz] = mock!!
        }
        return mocks[clazz] as T
    }

    fun clearMocks() {
        mocks.clear()
    }
}