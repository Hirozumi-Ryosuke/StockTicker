package com.example.stockticker.ticker.ui

import android.graphics.Canvas
import com.example.stockticker.ticker.AppPreferences
import com.example.stockticker.ticker.AppPreferences.Companion.DECIMAL_FORMAT
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.time.LocalDate
import java.time.LocalDate.ofEpochDay

class DateAxisFormatter : IAxisValueFormatter {

    override fun getFormattedValue(
        value: Float,
        axis: AxisBase
    ): String {
        val date = ofEpochDay(value.toLong())
        return date.format(AppPreferences.AXIS_DATE_FORMATTER)
    }
}

class ValueAxisFormatter : IAxisValueFormatter {

    override fun getFormattedValue(
        value: Float,
        axis: AxisBase
    ): String =
        DECIMAL_FORMAT.format(value)
}

class MultilineXAxisRenderer(
    viewPortHandler: ViewPortHandler?,
    xAxis: XAxis?,
    trans: Transformer?
) : XAxisRenderer(viewPortHandler, xAxis, trans) {

    override fun drawLabel(
        c: Canvas,
        formattedLabel: String,
        x: Float,
        y: Float,
        anchor: MPPointF,
        angleDegrees: Float
    ) {
        val lines = formattedLabel.split("-")
        for (i in 0 until lines.size) {
            val vOffset = i * mAxisLabelPaint.textSize
            Utils.drawXAxisValue(c, lines[i], x, y + vOffset, mAxisLabelPaint, anchor, angleDegrees)
        }
    }
}