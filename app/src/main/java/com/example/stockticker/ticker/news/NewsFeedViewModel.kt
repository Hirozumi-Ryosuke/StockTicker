package com.example.stockticker.ticker.news

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.model.FetchResult
import com.example.stockticker.ticker.network.NewsProvider
import com.example.stockticker.ticker.network.data.NewsArticle
import kotlinx.coroutines.launch
import javax.inject.Inject

class NewsFeedViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var newsProvider: NewsProvider

    init {
        Injector.appComponent.inject(this)
    }

    val newsFeed: LiveData<FetchResult<List<NewsArticle>>>
        get() = _newsFeed
    private val _newsFeed = MutableLiveData<FetchResult<List<NewsArticle>>>()

    fun fetchNews(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            val result = newsProvider.fetchBusinessNews(useCache = !forceRefresh)
            _newsFeed.value = result
        }
    }
}