package com.example.stockticker.ticker.model

import android.app.AlarmManager
import android.app.AlarmManager.*
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.*
import androidx.annotation.RequiresApi
import com.example.stockticker.ticker.AppPreferences
import com.example.stockticker.ticker.components.AppClock
import com.example.stockticker.ticker.components.Injector
import com.example.stockticker.ticker.widget.RefreshReceiver
import timber.log.Timber
import timber.log.Timber.*
import java.time.Duration
import java.time.Duration.*
import java.time.Instant
import java.time.Instant.*
import java.time.ZoneId
import java.time.ZoneId.*
import java.time.ZonedDateTime
import java.time.ZonedDateTime.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmScheduler {

    @Inject
    internal lateinit var appPreferences: AppPreferences
    @Inject internal lateinit var clock: AppClock

    init {
        Injector.appComponent.inject(this)
    }

    /**
     * Takes care of weekends and after hours
     */
    @RequiresApi(O)
    fun msToNextAlarm(lastFetchedMs: Long): Long {
        val dayOfWeek = clock.todayLocal()
            .dayOfWeek
        val startTimez = appPreferences.startTime()
        val endTimez = appPreferences.endTime()
        // whether the start time is after the end time e.g. start time is 11pm and end time is 5am
        val inverse =
            startTimez[0] > endTimez[0] || startTimez[0] == endTimez[0] && startTimez[1] > endTimez[1]
        val now = clock.todayZoned()
        val startTime = clock.todayZoned()
            .withHour(startTimez[0])
            .withMinute(startTimez[1])
        var endTime = clock.todayZoned()
            .withHour(endTimez[0])
            .withMinute(endTimez[1])
        if (inverse && now.isAfter(startTime)) {
            endTime = endTime.plusDays(1)
        }
        val selectedDaysOfWeek = appPreferences.updateDays()
        val lastFetchedTime =
            ofInstant(ofEpochMilli(lastFetchedMs), systemDefault())

        var nextAlarmDate = clock.todayZoned()
        if (now.isBefore(endTime) && (now.isAfter(startTime) || now.isEqual(
                startTime
            )) && selectedDaysOfWeek.contains(dayOfWeek)
        ) {
            nextAlarmDate = if (lastFetchedMs > 0
                && between(lastFetchedTime,now).toMillis() >= appPreferences.updateIntervalMs
            ) {
                nextAlarmDate.plusMinutes(1)
            } else {
                nextAlarmDate.plusSeconds(appPreferences.updateIntervalMs / 1000L)
            }
        } else if (!inverse && now.isBefore(startTime) && selectedDaysOfWeek.contains(dayOfWeek)) {
            nextAlarmDate = if (lastFetchedMs > 0 && lastFetchedTime.isBefore(endTime.minusDays(1))) {
                nextAlarmDate.plusMinutes(1)
            } else {
                nextAlarmDate.withHour(startTimez[0])
                    .withMinute(startTimez[1])
            }
        } else {
            if (selectedDaysOfWeek.contains(dayOfWeek) && lastFetchedMs > 0 && lastFetchedTime.isBefore(endTime)) {
                nextAlarmDate = nextAlarmDate.plusMinutes(1)
            } else {
                nextAlarmDate = nextAlarmDate.withHour(startTimez[0])
                    .withMinute(startTimez[1])

                var count = 0
                if (inverse) {
                    while (!selectedDaysOfWeek.contains(nextAlarmDate.dayOfWeek) && count <= 7) {
                        count++
                        nextAlarmDate = nextAlarmDate.plusDays(1)
                    }
                } else {
                    do {
                        count++
                        nextAlarmDate = nextAlarmDate.plusDays(1)
                    } while (!selectedDaysOfWeek.contains(nextAlarmDate.dayOfWeek) && count <= 7)
                }

                if (count >= 7) {
                    w(Exception("Possible infinite loop in calculating date. Now: ${now.toInstant()}, nextUpdate: ${nextAlarmDate.toInstant()}"))
                }
            }
        }

        val msToNextAlarm = nextAlarmDate.toInstant().toEpochMilli() - now.toInstant().toEpochMilli()
        return msToNextAlarm
    }

    @RequiresApi(O)
    fun scheduleUpdate(
        msToNextAlarm: Long,
        context: Context
    ): ZonedDateTime {
        i("Scheduled for ${msToNextAlarm / (1000 * 60)} minutes")
        val updateReceiverIntent = Intent(context, RefreshReceiver::class.java)
        updateReceiverIntent.action = AppPreferences.UPDATE_FILTER
        val instant = ofEpochMilli(clock.currentTimeMillis() + msToNextAlarm)
        val nextAlarmDate = ofInstant(instant, systemDefault())
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent =
            PendingIntent.getBroadcast(
                context.applicationContext, 0, updateReceiverIntent,
                FLAG_UPDATE_CURRENT
            )
        alarmManager.setExact(
            ELAPSED_REALTIME_WAKEUP,
            clock.elapsedRealtime() + msToNextAlarm, pendingIntent
        )
        return nextAlarmDate
    }
}