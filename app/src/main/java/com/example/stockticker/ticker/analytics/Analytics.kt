package com.example.stockticker.ticker.analytics

import android.app.Activity
import android.content.Context
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.components.Injector.appComponent
import com.example.stockticker.ticker.model.IStocksProvider
import com.example.stockticker.ticker.widget.WidgetDataProvider
import javax.inject.Inject

interface Analytics {
    fun initialize(context: Context) {}
    fun trackScreenView(screenName: String, activity: Activity) {}
    fun trackClickEvent(event: ClickEvent) {}
    fun trackGeneralEvent(event: GeneralEvent) {}
}

sealed class AnalyticsEvent(val name: String) {

    val properties: Map<String, String>
        get() = _properties
    private val _properties = HashMap<String, String>()

    open fun addProperty(key: String, value: String) = apply {
        _properties[key] = value
    }
}

class GeneralEvent(name: String): AnalyticsEvent(name) {
    override fun addProperty(key: String, value: String) = apply {
        super.addProperty(key, value)
    }
}

class ClickEvent(name: String): AnalyticsEvent(name) {
    override fun addProperty(key: String, value: String) = apply {
        super.addProperty(key, value)
    }
}

class GeneralProperties {

    @Inject lateinit var widgetDataProvider: WidgetDataProvider
    @Inject
    lateinit var stocksProvider: IStocksProvider

    init {
        appComponent.inject(this)
    }

    val widgetCount
        get() = widgetDataProvider.getAppWidgetIds().size
    val tickerCount
        get() = stocksProvider.getTickers().size

}