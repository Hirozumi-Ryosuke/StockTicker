package com.example.stockticker.ticker.model

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.isNetworkOnline
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import timber.log.Timber.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class RefreshService : JobService(), CoroutineScope {

    @Inject
    internal lateinit var stocksProvider: IStocksProvider

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onCreate() {
        super.onCreate()
        Injector.appComponent.inject(this)
    }

    override fun onStartJob(params: JobParameters): Boolean {
        d("onStartJob ${params.jobId}")
        return if (isNetworkOnline()) {
            launch {
                stocksProvider.fetch()
                val needsReschedule = false
                jobFinished(params, needsReschedule)
            }
            // additional work is being performed
            true
        } else {
            stocksProvider.scheduleSoon()
            false
        }
    }

    override fun onStopJob(params: JobParameters): Boolean {
        d("onStopJob ${params.jobId}")
        // doesn't need reschedule
        return false
    }
}