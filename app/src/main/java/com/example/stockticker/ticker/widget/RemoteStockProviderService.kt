package com.example.stockticker.ticker.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Intent
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory

class RemoteStockProviderService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val appWidgetId =
            intent.getIntExtra(EXTRA_APPWIDGET_ID, INVALID_APPWIDGET_ID)
        return RemoteStockViewAdapter(appWidgetId)
    }
}