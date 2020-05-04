package com.example.stockticker.ticker.components

import android.content.Context
import com.example.stockticker.ticker.StocksApp
import com.example.stockticker.ticker.analytics.GeneralProperties
import com.example.stockticker.ticker.base.BaseFragment
import com.example.stockticker.ticker.home.HomeFragment
import com.example.stockticker.ticker.home.ParanormalActivity
import com.example.stockticker.ticker.news.GraphViewModel
import com.example.stockticker.ticker.news.QuoteDetailActivity
import com.example.stockticker.ticker.portfolio.StocksAdapter
import com.example.stockticker.ticker.portfolio.search.SearchActivity
import com.example.stockticker.ticker.settings.WidgetSettingsActivity
import com.example.stockticker.ticker.settings.WidgetSettingsFragment
import com.example.stockticker.ticker.widget.WidgetDataProvider
import com.google.gson.Gson

@javax.inject.Singleton
@dagger.Component(modules = arrayOf(AppModule::class))
interface AppComponent {

    // Activities

fun inject(paranormalActivity: ParanormalActivity)

fun inject(addPositionActivity: AddPositionActivity)

fun inject(splashActivity: SplashActivity)

fun inject(newsFeedActivity: QuoteDetailActivity)

fun inject(graphActivity: GraphActivity)

fun inject(searchActivity: SearchActivity)

fun inject(widgetSettingsActivity: WidgetSettingsActivity)

fun inject(dbViewerActivity: DbViewerActivity)

// Components

fun inject(stocksApp: StocksApp.InjectionHolder)

fun inject(stocksStorage: StocksStorage)

fun inject(appPreferences: AppPreferences)

fun inject(stocksProvider: StocksProvider)

fun inject(historicalDataProvider: HistoryProvider)

fun inject(alarmScheduler: AlarmScheduler)

fun inject(updateReceiver: UpdateReceiver)

fun inject(refreshReceiver: RefreshReceiver)

fun inject(refreshService: RefreshService)

fun inject(exponentialBackoff: ExponentialBackoff)

fun inject(generalProperties: GeneralProperties)

// Network

fun inject(stocksApi: StocksApi)

fun inject(newsProvider: NewsProvider)

fun inject(interceptor: UserAgentInterceptor)

// Widget

fun inject(stockWidget: StockWidget)

fun inject(widgetClickReceiver: WidgetClickReceiver)

fun inject(widgetDataProvider: WidgetDataProvider)

fun inject(widgetData: WidgetData)

fun inject(remoteStockViewAdapter: RemoteStockViewAdapter)

// UI

fun inject(holder: BaseFragment.InjectionHolder)

fun inject(holder: PortfolioFragment.InjectionHolder)

fun inject(homeAdapter: HomePagerAdapter)

fun inject(homeFragment: HomeFragment)

fun inject(fragment: SearchFragment)

fun inject(settingsFragment: SettingsFragment)

fun inject(stocksAdapter: StocksAdapter)

fun inject(fragment: WidgetsFragment)

fun inject(widgetSettingsFragment: WidgetSettingsFragment)

fun appContext(): Context

fun gson(): Gson

// ViewModels

fun inject(dbViewerViewModel: DbViewerViewModel)

fun inject(quoteDetailViewModel: QuoteDetailViewModel)

fun inject(graphViewModel: GraphViewModel)

fun inject(newsFeedViewModel: NewsFeedViewModel)
}