package com.example.stockticker.ticker.mock

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.example.stockticker.ticker.AppPreferences
import com.example.stockticker.ticker.StocksApp
import com.example.stockticker.ticker.analytics.Analytics
import com.example.stockticker.ticker.components.AppClock
import com.example.stockticker.ticker.components.AsyncBus
import com.example.stockticker.ticker.repo.QuoteDao
import com.example.stockticker.ticker.repo.QuotesDB
import com.example.stockticker.ticker.repo.StocksStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [MockNetworkModule::class])
class MockAppModule(private val app: StocksApp) {

    @Provides
    internal fun provideApplicationContext(): Context = app

    @Provides @Singleton
    internal fun provideClock(): AppClock = Mocker.provide(AppClock::class)

    @Provides @Singleton internal fun provideEventBus(): AsyncBus = AsyncBus()

    @Provides @Singleton internal fun provideMainThreadHandler(): Handler =
        Handler(Looper.getMainLooper())

    @Provides @Singleton internal fun provideDefaultSharedPreferences(
        context: Context): SharedPreferences {
        return context.getSharedPreferences(AppPreferences.PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Provides @Singleton internal fun provideAppWidgetManager(): AppWidgetManager =
        AppWidgetManager.getInstance(app)

    @Provides @Singleton internal fun provideAppPreferences(): AppPreferences = AppPreferences()

    @Provides @Singleton internal fun provideStorage(): StocksStorage =
        StocksStorage()

    @Provides @Singleton internal fun provideAnalytics(): Analytics = Mocker.provide(Analytics::class)

    @Provides @Singleton fun provideQuotesDB(context: Context): QuotesDB {
        return Mocker.provide(QuotesDB::class)
    }

    @Provides @Singleton fun provideQuoteDao(db: QuotesDB): QuoteDao {
        return Mocker.provide(QuoteDao::class)
    }
}