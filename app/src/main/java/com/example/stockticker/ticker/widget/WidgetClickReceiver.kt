package com.example.stockticker.ticker.widget

import android.appwidget.AppWidgetManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.stockticker.ticker.analytics.Analytics
import com.example.stockticker.ticker.analytics.ClickEvent
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.home.ParanormalActivity
import javax.inject.Inject

class WidgetClickReceiver : BroadcastReceiver() {

    @Inject
    internal lateinit var widgetDataProvider: WidgetDataProvider
    @Inject internal lateinit var analytics: Analytics

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        Injector.appComponent.inject(this)
        if (intent.getBooleanExtra(FLIP, false)) {
            val widgetId = intent.getIntExtra(WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            val widgetData = widgetDataProvider.dataForWidgetId(widgetId)
            widgetData.flipChange()
            widgetDataProvider.broadcastUpdateWidget(widgetId)
            analytics.trackClickEvent(ClickEvent("WidgetFlipClick"))
        } else {
            val startActivityIntent = Intent(context, ParanormalActivity::class.java)
            startActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(startActivityIntent)
            analytics.trackClickEvent(ClickEvent("WidgetClick"))
        }
    }

    companion object {

        const val CLICK_BCAST_INTENTFILTER = "com.github.premnirmal.ticker.WIDGET_CLICK"
        const val FLIP = "FLIP"
        const val WIDGET_ID = AppWidgetManager.EXTRA_APPWIDGET_ID
    }
}