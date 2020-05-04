package com.example.stockticker.ticker

import com.facebook.stetho.Stetho

object StethoInitializer {
    fun initialize(app: StocksApp) {
        Stetho.initializeWithDefaults(app)
    }
}