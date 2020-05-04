package com.example.stockticker.ticker.components

object Injector {

    lateinit var appComponent: AppComponent

    internal fun init(ac: AppComponent) {
        Injector.appComponent = ac
    }
}