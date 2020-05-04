package com.example.stockticker.ticker.network

import com.example.stockticker.ticker.network.data.SuggestionsNet
import retrofit2.http.GET
import retrofit2.http.Query

interface SuggestionApi {

    @GET("autoc?callback=YAHOO.Finance.SymbolSuggest.ssCallback&region=US&lang=en-US")
    suspend fun getSuggestions(@Query("query") query: String): SuggestionsNet

}