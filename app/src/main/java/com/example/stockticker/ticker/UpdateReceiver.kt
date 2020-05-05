package com.example.stockticker.ticker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.model.IStocksProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class UpdateReceiver : BroadcastReceiver(), CoroutineScope {

    @Inject
    internal lateinit var stocksProvider: IStocksProvider

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        Injector.appComponent.inject(this)
        launch {
            stocksProvider.fetch()
        }
    }
}