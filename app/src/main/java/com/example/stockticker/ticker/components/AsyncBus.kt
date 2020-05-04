package com.example.stockticker.ticker.components

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.intellij.lang.annotations.Flow
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class AsyncBus : CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    @ExperimentalCoroutinesApi
    val bus = BroadcastChannel<Any>(1)

    @ExperimentalCoroutinesApi
    fun send(event: Any): Boolean {
        if (bus.offer(event)) return true
        else launch {
            bus.send(event)
        }
        return false
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    inline fun <reified T> receive(): Flow<T> {
        return bus.openSubscription()
            .consumeAsFlow()
            .filter { it is T }
            .map { it as T }
    }
}