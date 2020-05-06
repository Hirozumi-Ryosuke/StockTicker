package com.example.stockticker.ticker.ui

import android.content.Context
import android.widget.TextView
import com.example.stockticker.R
import com.example.stockticker.R.id.tvContent
import com.example.stockticker.R.layout.text_marker_layout
import com.example.stockticker.ticker.AppPreferences
import com.example.stockticker.ticker.AppPreferences.Companion.DATE_FORMATTER
import com.example.stockticker.ticker.AppPreferences.Companion.DECIMAL_FORMAT
import com.example.stockticker.ticker.network.data.DataPoint
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF


class TextMarkerView(context: Context) : MarkerView(context, text_marker_layout) {

    private var tvContent: TextView = findViewById(R.id.tvContent)
    private val offsetPoint by lazy {
        MPPointF(-(width / 2).toFloat(), -height.toFloat())
    }

    override fun refreshContent(
        e: Entry?,
        highlight: Highlight?
    ) {
        if (e is DataPoint) {
            val price = DECIMAL_FORMAT.format(e.y)
            val date = e.getDate()
                .format(DATE_FORMATTER)
            tvContent.text = "${price}\n$date"
        } else {
            tvContent.text = ""
        }
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF = offsetPoint
}