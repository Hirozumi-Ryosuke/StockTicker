package com.example.stockticker.ticker.network

import androidx.multidex.BuildConfig
import com.example.stockticker.ticker.components.AppClock
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.model.FetchException
import com.example.stockticker.ticker.model.FetchResult
import com.example.stockticker.ticker.network.data.IQuoteNet
import com.example.stockticker.ticker.network.data.Quote
import com.example.stockticker.ticker.network.data.SuggestionsNet
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StocksApi {

    companion object {
        var DEBUG = BuildConfig.DEBUG
    }

    @Inject
    internal lateinit var gson: Gson
    @Inject internal lateinit var yahooFinance: YahooFinance
    @Inject internal lateinit var suggestionApi: SuggestionApi
    @Inject internal lateinit var clock: AppClock
    var lastFetched: Long = 0

    init {
        Injector.appComponent.inject(this)
    }

    suspend fun getSuggestions(query: String): FetchResult<List<SuggestionsNet.SuggestionNet>> =
        withContext(Dispatchers.IO) {
            val suggestions = try {
                suggestionApi.getSuggestions(query)
                    .resultSet?.result
            } catch (e: Exception) {
                Timber.w(e)
                return@withContext FetchResult.failure<List<SuggestionsNet.SuggestionNet>>(FetchException("Error fetching", e))
            }
            val suggestionList = suggestions?.let { ArrayList(it) } ?: ArrayList()
            return@withContext FetchResult.success<List<SuggestionsNet.SuggestionNet>>(suggestionList)
        }

    /**
     * Prefer robindahood, fallback to yahoo finance.
     */
    suspend fun getStocks(tickerList: List<String>): FetchResult<List<Quote>> =
        withContext(Dispatchers.IO) {
            try {
                val quoteNets = getStocksYahoo(tickerList)
                lastFetched = clock.currentTimeMillis()
                return@withContext FetchResult.success(quoteNets.toQuoteMap().toOrderedList(tickerList))
            } catch (ex: Exception) {
                Timber.w(ex)
                return@withContext FetchResult.failure<List<Quote>>(FetchException("Failed to fetch", ex))
            }
        }

    suspend fun getStock(ticker: String): FetchResult<Quote> =
        withContext(Dispatchers.IO) {
            try {
                val quoteNets = getStocksYahoo(listOf(ticker))
                return@withContext FetchResult.success(quoteNets.first().toQuote())
            } catch (ex: Exception) {
                Timber.w(ex)
                return@withContext FetchResult.failure<Quote>(FetchException("Failed to fetch $ticker", ex))
            }
        }

    private suspend fun getStocksYahoo(tickerList: List<String>) = withContext(Dispatchers.IO) {
        val query = tickerList.joinToString(",")
        val quoteNets = yahooFinance.getStocks(query).quoteResponse!!.result
        quoteNets
    }

    private fun List<IQuoteNet>.toQuoteMap(): MutableMap<String, Quote> {
        val quotesMap = HashMap<String, Quote>()
        for (quoteNet in this) {
            val quote = quoteNet.toQuote()
            quotesMap[quote.symbol] = quote
        }
        return quotesMap
    }

    private fun MutableMap<String, Quote>.toOrderedList(tickerList: List<String>): List<Quote> {
        val quotes = ArrayList<Quote>()
        tickerList.filter { this.containsKey(it) }
            .mapTo(quotes) { this[it]!! }
        return quotes
    }

    private fun IQuoteNet.toQuote(): Quote {
        val quote = Quote(this.symbol ?: "")
        quote.name = this.name ?: ""
        quote.lastTradePrice = this.lastTradePrice
        quote.changeInPercent = this.changePercent
        quote.change = this.change
        quote.stockExchange = this.exchange ?: ""
        quote.currency = this.currency ?: "US"
        quote.description = this.description ?: ""
        return quote
    }
}