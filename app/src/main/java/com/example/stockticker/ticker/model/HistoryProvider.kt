package com.example.stockticker.ticker.model

import android.os.Build
import android.os.Build.VERSION_CODES.*
import android.util.Range
import androidx.annotation.RequiresApi
import com.example.stockticker.R
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.home.IHistoryProvider
import com.example.stockticker.ticker.network.DataPoint
import com.example.stockticker.ticker.network.HistoricalDataApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import timber.log.Timber
import timber.log.Timber.*
import java.lang.ref.WeakReference
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.*
import javax.inject.Inject

class HistoryProvider : IHistoryProvider {

    @Inject
    internal lateinit var historicalDataApi: HistoricalDataApi
    private val apiKey = Injector.appComponent.appContext()
        .getString(R.string.alpha_vantage_api_key)

    private var cachedData: WeakReference<Pair<String, List<DataPoint>>>? = null

    init {
        Injector.appComponent.inject(this)
    }

    @RequiresApi(O)
    override suspend fun getHistoricalDataShort(symbol: String): FetchResult<List<DataPoint>> {
        return withContext(IO) {
            val dataPoints = try {
                val historicalData = historicalDataApi.getHistoricalData(apiKey = apiKey, symbol = symbol)
                val points = ArrayList<DataPoint>()
                historicalData.timeSeries.forEach { entry ->
                    val epochDate = LocalDate.parse(entry.key, ISO_LOCAL_DATE)
                        .toEpochDay()
                    points.add(DataPoint(epochDate.toFloat(), entry.value.close.toFloat()))
                }
                points.sorted()
            } catch (ex: Exception) {
                w(ex)
                return@withContext FetchResult.failure<List<DataPoint>>(FetchException("Error fetching datapoints", ex))
            }
            FetchResult.success(dataPoints)
        }
    }

    @RequiresApi(O)
    override suspend fun getHistoricalDataByRange(
        symbol: String,
        range: Range
    ) = withContext(IO) {
        val dataPoints = try {
            if (symbol == cachedData?.get()?.first) {
                cachedData!!.get()!!.second.filter {
                        it.getDate()
                            .isAfter(range.end)
                    }
                    .toMutableList()
                    .sorted()
            } else {
                cachedData = null
                val historicalData =
                    historicalDataApi.getHistoricalDataFull(apiKey = apiKey, symbol = symbol)
                val points = ArrayList<DataPoint>()
                historicalData.timeSeries.forEach { entry ->
                    val epochDate = LocalDate.parse(entry.key, ISO_LOCAL_DATE)
                        .toEpochDay()
                    points.add(DataPoint(epochDate.toFloat(), entry.value.close.toFloat()))
                }
                cachedData = WeakReference(Pair(symbol, points))
                points.filter {
                        it.getDate()
                            .isAfter(range.end)
                    }
                    .toMutableList()
                    .sorted()
            }
        } catch (ex: Exception) {
            w(ex)
            return@withContext FetchResult.failure<List<DataPoint>>(FetchException("Error fetching datapoints", ex))
        }
         FetchResult.success(dataPoints)
    }
}