package com.example.stockticker.ticker.components

import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.ZonedDateTime

interface AppClock {
    fun todayZoned(): ZonedDateTime
    fun todayLocal(): LocalDateTime
    fun currentTimeMillis(): Long
    fun elapsedRealtime(): Long

    class AppClockImpl : AppClock {

        @RequiresApi(Build.VERSION_CODES.O)
        override fun todayZoned(): ZonedDateTime = ZonedDateTime.now()

        @RequiresApi(Build.VERSION_CODES.O)
        override fun todayLocal(): LocalDateTime = LocalDateTime.now()

        override fun currentTimeMillis(): Long = System.currentTimeMillis()

        override fun elapsedRealtime(): Long = SystemClock.elapsedRealtime()
    }
}