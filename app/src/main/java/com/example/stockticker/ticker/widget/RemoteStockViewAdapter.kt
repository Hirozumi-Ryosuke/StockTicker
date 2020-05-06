package com.example.stockticker.ticker.widget

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.stockticker.R
import com.example.stockticker.R.id.*
import com.example.stockticker.R.integer.*
import com.example.stockticker.R.layout.loadview
import com.example.stockticker.R.layout.stockview3
import com.example.stockticker.ticker.AppPreferences
import com.example.stockticker.ticker.AppPreferences.Companion.FONT_SIZE
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.network.data.Quote
import timber.log.Timber
import javax.inject.Inject

class RemoteStockViewAdapter(private val widgetId: Int) : RemoteViewsService.RemoteViewsFactory {

    private val quotes: MutableList<Quote>

    @Inject
    internal lateinit var widgetDataProvider: WidgetDataProvider
    @Inject internal lateinit var context: Context
    @Inject internal lateinit var sharedPreferences: SharedPreferences

    init {
        this.quotes = ArrayList()
    }

    override fun onCreate() {
        Injector.appComponent.inject(this)
        val widgetData = widgetDataProvider.dataForWidgetId(widgetId)
        val stocks = widgetData.getStocks()
        quotes.clear()
        quotes.addAll(stocks)
    }

    override fun onDataSetChanged() {
        val widgetData = widgetDataProvider.dataForWidgetId(widgetId)
        val stocksList = widgetData.getStocks()
        this.quotes.clear()
        this.quotes.addAll(stocksList)
    }

    override fun onDestroy() {
        quotes.clear()
    }

    override fun getCount(): Int = quotes.size

    override fun getViewAt(position: Int): RemoteViews {
        val widgetData = widgetDataProvider.dataForWidgetId(widgetId)
        val stockViewLayout = widgetData.stockViewLayout()
        val remoteViews = RemoteViews(context.packageName, stockViewLayout)
        try {
            val stock = quotes[position]

            val changeValueFormatted = stock.changeString()
            val changePercentFormatted = stock.changePercentString()
            val priceFormatted = stock.priceString()
            val change = stock.change
            val changeInPercent = stock.changeInPercent

            val changePercentString = SpannableString(changePercentFormatted)
            val changeValueString = SpannableString(changeValueFormatted)
            val priceString = SpannableString(priceFormatted)

            remoteViews.setTextViewText(ticker, stock.symbol)

            if (widgetData.isBoldEnabled()) {
                changePercentString.setSpan(
                    StyleSpan(BOLD), 0, changePercentString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                changeValueString.setSpan(
                    StyleSpan(BOLD), 0, changeValueString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                changePercentString.setSpan(
                    StyleSpan(NORMAL), 0, changePercentString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                changeValueString.setSpan(
                    StyleSpan(NORMAL), 0, changeValueString.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            if (stockViewLayout == stockview3) {
                val changeType = widgetData.changeType()
                if (changeType === WidgetData.Companion.ChangeType.Percent) {
                    remoteViews.setTextViewText(R.id.change, changePercentString)
                } else {
                    remoteViews.setTextViewText(R.id.change, changeValueString)
                }
            } else {
                remoteViews.setTextViewText(changePercent, changePercentString)
                remoteViews.setTextViewText(changeValue, changeValueString)
            }
            remoteViews.setTextViewText(totalValue, priceString)

            val color: Int
            color = if (change < 0f || changeInPercent < 0f) {
                context.getColor(widgetData.negativeTextColor)
            } else {
                context.getColor(widgetData.positiveTextColor)
            }
            if (stockViewLayout == stockview3) {
                remoteViews.setTextColor(R.id.change, color)
            } else {
                remoteViews.setTextColor(changePercent, color)
                remoteViews.setTextColor(changeValue, color)
            }

            remoteViews.setTextColor(ticker, widgetData.textColor())
            remoteViews.setTextColor(totalValue, widgetData.textColor())

            val fontSize = getFontSize()
            if (stockViewLayout == stockview3) {
                remoteViews.setTextViewTextSize(R.id.change, COMPLEX_UNIT_SP, fontSize)
            } else {
                remoteViews.setTextViewTextSize(changePercent, COMPLEX_UNIT_SP, fontSize)
                remoteViews.setTextViewTextSize(changeValue, COMPLEX_UNIT_SP, fontSize)
            }
            remoteViews.setTextViewTextSize(ticker, COMPLEX_UNIT_SP, fontSize)
            remoteViews.setTextViewTextSize(totalValue, COMPLEX_UNIT_SP, fontSize)

            if (stockViewLayout == stockview3) {
                val intent = Intent()
                intent.putExtra(WidgetClickReceiver.FLIP, true)
                intent.putExtra(WidgetClickReceiver.WIDGET_ID, widgetId)
                remoteViews.setOnClickFillInIntent(R.id.change, intent)
            }
            remoteViews.setOnClickFillInIntent(ticker, Intent())
        } catch (t: Throwable) {
            Timber.w(t)
        }
        return remoteViews
    }

    override fun getLoadingView(): RemoteViews {
        val loadingView = RemoteViews(context.packageName, loadview)
        val widgetData = widgetDataProvider.dataForWidgetId(widgetId)
        loadingView.setTextColor(loadingText, widgetData.textColor())
        return loadingView
    }

    override fun getViewTypeCount(): Int = 3

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true

    private fun getFontSize(): Float {
        val size = sharedPreferences.getInt(FONT_SIZE, 1)
        return when (size) {
            0 -> this.context.resources.getInteger(text_size_small).toFloat()
            2 -> this.context.resources.getInteger(text_size_large).toFloat()
            else -> this.context.resources.getInteger(text_size_medium).toFloat()
        }
    }
}