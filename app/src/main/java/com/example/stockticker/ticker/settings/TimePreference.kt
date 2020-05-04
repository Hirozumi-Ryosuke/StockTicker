package com.example.stockticker.ticker.settings

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference
import java.lang.Integer.*

class TimePreference(
    context: Context,
    attrs: AttributeSet
) : DialogPreference(context, attrs) {

    var lastHour = 0
    var lastMinute = 0

    override fun onGetDefaultValue(
        a: TypedArray,
        index: Int
    ): Any? = a.getString(index)

    override fun onSetInitialValue(
        restoreValue: Boolean,
        defaultValue: Any?
    ) {
        super.onSetInitialValue(defaultValue)
        val time: String = if (restoreValue) {
            if (defaultValue == null) {
                getPersistedString("00:00")
            } else {
                getPersistedString("$defaultValue")
            }
        } else {
            defaultValue.toString()
        }
        lastHour = getHour(time)
        lastMinute = getMinute(time)
    }

    companion object {

        fun getHour(time: String): Int {
            val pieces = time.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            return parseInt(pieces[0])
        }

        fun getMinute(time: String): Int {
            val pieces = time.split(":".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
            return parseInt(pieces[1])
        }
    }

}