package com.example.stockticker.ticker.model

import android.os.Build
import android.os.Build.VERSION_CODES.*
import androidx.annotation.RequiresApi
import com.example.stockticker.ticker.network.DataPoint
import java.time.LocalDate
import java.io.Serializable
import java.time.LocalDate.now

interface IHistoryProvider {

    suspend fun getHistoricalDataShort(symbol: String): FetchResult<List<DataPoint>>

    suspend fun getHistoricalDataByRange(
        symbol: String,
        range: Range
    ): FetchResult<List<DataPoint>>

    sealed class Range(val end: LocalDate) : Serializable {
        class DateRange(end: LocalDate) : Range(end)
        companion object {
            @RequiresApi(O)
            val ONE_MONTH = DateRange(now().minusMonths(1))
            @RequiresApi(O)
            val THREE_MONTH = DateRange(now().minusMonths(3))
            @RequiresApi(O)
            val ONE_YEAR = DateRange(now().minusYears(1))
            @RequiresApi(O)
            val MAX = DateRange(now().minusYears(20))
        }
    }
}