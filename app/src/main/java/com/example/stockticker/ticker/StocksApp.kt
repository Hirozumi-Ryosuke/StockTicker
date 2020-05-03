package com.example.stockticker.ticker

import androidx.appcompat.app.AppCompatDelegate
import com.example.stockticker.R
import kotlinx.android.synthetic.*

open class StocksApp : MultiDexApplication() {

    class InjectionHolder {
        @Inject lateinit var analytics: Analytics
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