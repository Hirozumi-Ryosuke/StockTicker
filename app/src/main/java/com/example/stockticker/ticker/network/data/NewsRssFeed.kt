package com.example.stockticker.ticker.network.data

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import retrofit2.http.Path

@Root(name = "rss", strict = false)
class NewsRssFeed {
    @get:ElementList(name = "item", inline = true)
    @get:Path("channel")
    @set:ElementList(name = "item", inline = true)
    @set:Path("channel")
    var articleList: List<NewsArticle>? = null
}