package com.example.stockticker.ticker.components

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import com.example.stockticker.ticker.AppPreferences
import com.example.stockticker.ticker.StocksApp
import com.example.stockticker.ticker.analytics.Analytics
import com.example.stockticker.ticker.analytics.AnalyticsImpl
import com.example.stockticker.ticker.network.NetworkModule
import com.example.stockticker.ticker.repo.QuoteDao
import com.example.stockticker.ticker.repo.StocksStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
class AppModule(private val app: StocksApp) {

    @Provides
    fun provideApplicationContext(): Context = app

    @Provides @Singleton
    fun provideClock(): AppClock = AppClock.AppClockImpl()

    @Provides @Singleton fun provideEventBus(): AsyncBus = AsyncBus()

    @Provides @Singleton fun provideMainThreadHandler(): Handler =
        Handler(Looper.getMainLooper())

    @Provides @Singleton fun provideDefaultSharedPreferences(
        context: Context
    ): SharedPreferences =
        context.getSharedPreferences(AppPreferences.PREFS_NAME, MODE_PRIVATE)

    @Provides @Singleton fun provideAppWidgetManager(): AppWidgetManager =
        AppWidgetManager.getInstance(app)

    @Provides @Singleton fun provideAppPreferences(): AppPreferences = AppPreferences()

    @Provides @Singleton fun provideAnalytics(): Analytics = AnalyticsImpl()

    @Provides @Singleton fun provideStorage(): StocksStorage =
        StocksStorage()

    @Provides @Singleton fun provideQuotesDB(context: Context): QuotesDB {
        return Room.databaseBuilder(
            context.applicationContext,
            QuotesDB::class.java, "quotes-db"
        ).build()
    }

    @Provides @Singleton fun provideQuoteDao(db: QuotesDB): QuoteDao {
        return db.quoteDao()
    }
}