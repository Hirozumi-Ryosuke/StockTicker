package com.example.stockticker.ticker.widget

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.Intent.URI_INTENT_SCHEME
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.RemoteViews
import com.example.stockticker.R
import com.example.stockticker.R.id.*
import com.example.stockticker.R.layout.widget_empty_view
import com.example.stockticker.ticker.AppPreferences
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.components.Injector.appComponent
import com.example.stockticker.ticker.home.ParanormalActivity
import com.example.stockticker.ticker.model.IStocksProvider
import javax.inject.Inject

class StockWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_NAME = "OPEN_APP"
    }

    @Inject
    internal lateinit var stocksProvider: IStocksProvider
    @Inject internal lateinit var widgetDataProvider: WidgetDataProvider
    @Inject internal lateinit var appPreferences: AppPreferences

    var injected = false

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
        if (!injected) {
            appComponent.inject(this)
            injected = true
        }
        super.onReceive(context, intent)
        if (intent.action == ACTION_NAME) {
            context.startActivity(Intent(context, ParanormalActivity::class.java))
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (widgetId in appWidgetIds) {
            val minimumWidth: Int
            minimumWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val options = appWidgetManager.getAppWidgetOptions(widgetId)
                getMinWidgetWidth(options)
            } else {
                appWidgetManager.getAppWidgetInfo(widgetId)?.minWidth ?: 0
            }
            val remoteViews: RemoteViews = createRemoteViews(context, minimumWidth)
            updateWidget(context, widgetId, remoteViews, appWidgetManager)
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, list)
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val minimumWidth = getMinWidgetWidth(newOptions)
        val remoteViews: RemoteViews = createRemoteViews(context, minimumWidth)
        updateWidget(context, appWidgetId, remoteViews, appWidgetManager)
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        if (!injected) {
            appComponent.inject(this)
            injected = true
        }
        if (stocksProvider.nextFetchMs() <= 0) {
            stocksProvider.schedule()
        }
    }

    override fun onDeleted(
        context: Context?,
        appWidgetIds: IntArray?
    ) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds?.let { id ->
            id.forEach { widgetId ->
                val removed = widgetDataProvider.removeWidget(widgetId)
                removed?.getTickers()?.forEach { ticker ->
                    if (!widgetDataProvider.containsTicker(ticker)) {
                        stocksProvider.removeStock(ticker)
                    }
                }
            }
        }
    }

    private fun createRemoteViews(
        context: Context,
        min_width: Int
    ): RemoteViews = when {
        min_width > 750 -> RemoteViews(context.packageName, layout.widget_4x1)
        min_width > 500 -> RemoteViews(context.packageName, layout.widget_3x1)
        min_width > 250 -> // 3x2
            RemoteViews(context.packageName, layout.widget_2x1)
        else -> // 2x1
            RemoteViews(context.packageName, layout.widget_1x1)
    }

    private fun getMinWidgetWidth(options: Bundle?): Int {
        return if (options == null || !options.containsKey(
                AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH
            )
        ) {
            0 // 2x1
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                options.get(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) as Int
            } else {
                0
            }
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetId: Int,
        remoteViews: RemoteViews,
        appWidgetManager: AppWidgetManager
    ) {
        val widgetData = widgetDataProvider.dataForWidgetId(appWidgetId)
        val widgetAdapterIntent = Intent(context, RemoteStockProviderService::class.java)
        widgetAdapterIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        widgetAdapterIntent.data = Uri.parse(widgetAdapterIntent.toUri(URI_INTENT_SCHEME))

        remoteViews.setRemoteAdapter(list, widgetAdapterIntent)
        remoteViews.setEmptyView(list, widget_empty_view)
        val intent = Intent(context, WidgetClickReceiver::class.java)
        intent.action = WidgetClickReceiver.CLICK_BCAST_INTENTFILTER
        val flipIntent =
            PendingIntent.getBroadcast(context, 0, intent, FLAG_UPDATE_CURRENT)
        remoteViews.setPendingIntentTemplate(list, flipIntent)
        val lastFetched: String = stocksProvider.lastFetched()
        val lastUpdatedText = context.getString(R.string.last_fetch, lastFetched)
        remoteViews.setTextViewText(last_updated, lastUpdatedText)
        val nextUpdate: String = stocksProvider.nextFetch()
        val nextUpdateText: String = context.getString(R.string.next_fetch, nextUpdate)
        remoteViews.setTextViewText(next_update, nextUpdateText)
        remoteViews.setInt(R.id.widget_layout, "setBackgroundResource", widgetData.backgroundResource())
        // Refresh icon and progress
        val refreshing = appPreferences.isRefreshing()
        if (refreshing) {
            remoteViews.setViewVisibility(refresh_progress, VISIBLE)
            remoteViews.setViewVisibility(refresh_icon, GONE)
        } else {
            remoteViews.setViewVisibility(refresh_progress, GONE)
            remoteViews.setViewVisibility(refresh_icon, VISIBLE)
        }
        // Show/hide header
        val hideHeader = widgetData.hideHeader()
        if (hideHeader) {
            remoteViews.setViewVisibility(widget_header, GONE)
        } else {
            remoteViews.setViewVisibility(widget_header, VISIBLE)
        }
        val updateReceiverIntent = Intent(context, RefreshReceiver::class.java)
        updateReceiverIntent.action = AppPreferences.UPDATE_FILTER
        val refreshPendingIntent =
            PendingIntent.getBroadcast(
                context.applicationContext, 0, updateReceiverIntent,
                FLAG_UPDATE_CURRENT
            )
        remoteViews.setOnClickPendingIntent(refresh_icon, refreshPendingIntent)
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }
}