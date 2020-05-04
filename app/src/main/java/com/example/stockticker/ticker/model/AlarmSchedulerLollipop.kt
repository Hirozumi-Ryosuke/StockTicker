package com.example.stockticker.ticker.model

import android.app.job.JobInfo
import android.app.job.JobInfo.*
import android.app.job.JobScheduler
import android.app.job.JobScheduler.*
import android.content.ComponentName
import android.content.Context
import android.content.Context.*
import android.os.Build
import android.os.Build.VERSION.*
import android.os.Build.VERSION_CODES.*
import androidx.annotation.RequiresApi
import timber.log.Timber
import timber.log.Timber.*

@RequiresApi(LOLLIPOP)
internal object AlarmSchedulerLollipop {

    private val JOB_ID_SCHEDULE = 8424
    private val FIVE_MINUTES_MS = 5 * 60 * 1000L

    internal fun scheduleUpdate(
        msToNextAlarm: Long,
        context: Context
    ) {
        val jobScheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val componentName = ComponentName(context, RefreshService::class.java)
        val builder = Builder(JOB_ID_SCHEDULE, componentName)
        builder.setPersisted(true)
            .setRequiredNetworkType(NETWORK_TYPE_ANY)
            .setRequiresDeviceIdle(false)
            .setRequiresCharging(false)
            .setMinimumLatency(msToNextAlarm)
            .setOverrideDeadline(msToNextAlarm + FIVE_MINUTES_MS)
        if (SDK_INT >= O) {
            builder.setRequiresBatteryNotLow(false)
                .setRequiresStorageNotLow(false)
        }
        val jobInfo = builder.build()
        val scheduled = jobScheduler.schedule(jobInfo) == RESULT_SUCCESS
        if (!scheduled) {
            e(Exception("Job schedule failed!"))
        }
    }

}