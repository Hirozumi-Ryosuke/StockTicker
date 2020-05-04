package com.example.stockticker.ticker.network

import android.content.Context
import android.os.Build
import androidx.multidex.BuildConfig
import com.example.stockticker.ticker.components.Injector
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class UserAgentInterceptor : Interceptor {

    companion object {
        private const val USER_AGENT_FORMAT = "%s/%s(%s) (Android %s; %s %s)"
        private const val USER_AGENT_KEY = "UserAgent"
    }

    @Inject
    internal lateinit var context: Context

    private val userAgent by lazy {
        String.format(
            USER_AGENT_FORMAT, context.packageName, BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE, Build.VERSION.SDK_INT, Build.MANUFACTURER, Build.MODEL
        )
    }

    init {
        Injector.appComponent.inject(this)
    }

    @Throws(IOException::class) override fun intercept(chain: io.github.inflationx.viewpump.Interceptor.Chain): Response {
        val newRequest = chain.request()
            .newBuilder()
            .addHeader(USER_AGENT_KEY, userAgent)
            .build()
        return chain.proceed(newRequest)
    }

}