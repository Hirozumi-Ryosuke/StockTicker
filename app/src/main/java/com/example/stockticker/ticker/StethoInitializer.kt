package com.example.stockticker.ticker

object StethoInitializer {
    fun initialize(app: StocksApp) {
        Stetho.initializeWithDefaults(app)
    }
}