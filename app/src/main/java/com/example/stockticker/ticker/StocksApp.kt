package com.example.stockticker.ticker

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.example.stockticker.R
import com.example.stockticker.ticker.analytics.Analytics
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.network.NewsProvider
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.viewpump.ViewPump
import kotlinx.android.synthetic.*
import javax.inject.Inject

open class StocksApp : MultiDexApplication() {

    class InjectionHolder {
        @Inject
        lateinit var analytics: Analytics
        @Inject lateinit var appPreferences: AppPreferences
        @Inject lateinit var newsProvider: NewsProvider
    }

    private val holder = InjectionHolder()

    override fun onCreate() {
        super.onCreate()
        initLogger()
        initThreeTen()
        ViewPump.init(
            ViewPump.builder()
                .appInterceptor(
                    CalligraphyIntercepter(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("fonts/Ubuntu-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()))
                .build())
        Injector.init(createAppComponent())
        Injector.appComponent.inject(holder)
        AppCompatDelegate.setDefaultNightMode(holder.appPreferences.nightMode)
        initAnalytics()
        if(BuildConfig.DEBUG) {
            initStetho()
        }
        initNewsCache()
    }

    open fun initStetho() {
        StethoInitializer.initialize(this)
    }

    open fun createAppComponent(): AppComponent {
        return DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    protected open fun initLogger() {
        Timber.plant(LoggingTree(this))
    }

    protected open fun initAnalytics() {
        holder.analytics.initialize(this)
    }

    protected open fun initNewsCache() {
        holder.newsProvider.initCache()
    }
}