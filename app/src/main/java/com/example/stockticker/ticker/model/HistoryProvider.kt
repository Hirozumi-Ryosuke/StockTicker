package com.example.stockticker.ticker.model

import android.os.Build.VERSION_CODES.*
import androidx.annotation.RequiresApi
import com.example.stockticker.R.string.*
import com.example.stockticker.ticker.components.Injector.appComponent
import com.example.stockticker.ticker.model.FetchResult.Companion.failure
import com.example.stockticker.ticker.network.data.DataPoint
import com.example.stockticker.ticker.network.HistoricalDataApi
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import timber.log.Timber.*
import java.lang.ref.WeakReference
import java.time.LocalDate.parse
import java.time.format.DateTimeFormatter.*
import javax.inject.Inject


class HistoryProvider : IHistoryProvider {

    @Inject
    internal lateinit var historicalDataApi: HistoricalDataApi
    private val apiKey = appComponent.appContext()
        .getString(alpha_vantage_api_key)

    private var cachedData: WeakReference<Pair<String, List<DataPoint>>>? = null

    init {
        appComponent.inject(this)
    }

    @RequiresApi(O)
    override suspend fun getHistoricalDataShort(symbol: String): FetchResult<List<DataPoint>> {
        return withContext(IO) {
            val dataPoints = try {
                val historicalData = historicalDataApi.getHistoricalData(apiKey = apiKey, symbol = symbol)
                val points = ArrayList<DataPoint>()
                historicalData.timeSeries.forEach { entry ->
                    val epochDate = parse(entry.key, ISO_LOCAL_DATE)
                        .toEpochDay()
                    points.add(
                        DataPoint(
                            epochDate.toFloat(),
                            entry.value.close.toFloat()
                        )
                    )
                }
                points.sorted()
            } catch (ex: Exception) {
                w(ex)
                return@withContext failure<List<DataPoint>>(FetchException("Error fetching datapoints", ex))
            }
            FetchResult.success(dataPoints)
        }
    }

    @RequiresApi(O)
    override suspend fun getHistoricalDataByRange(
        symbol: String,
        range: IHistoryProvider.Range
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
                    val epochDate = parse(entry.key, ISO_LOCAL_DATE)
                        .toEpochDay()
                    points.add(
                        DataPoint(
                            epochDate.toFloat(),
                            entry.value.close.toFloat()
                        )
                    )
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
            return@withContext failure<List<DataPoint>>(FetchException("Error fetching datapoints", ex))
        }
         FetchResult.success(dataPoints)
    }
}