package com.personalfinance.sdk.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

object SyncScheduler {
    private const val UNIQUE_WORK_NAME = "daily-sheet-sync"

    fun scheduleDaily(context: Context, syncTime: LocalTime) {
        val initialDelay = computeInitialDelay(syncTime)
        val work = PeriodicWorkRequestBuilder<DailySheetSyncWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelay)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build(),
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            work,
        )
    }

    private fun computeInitialDelay(syncTime: LocalTime): Duration {
        val now = ZonedDateTime.now()
        var nextRun = now.withHour(syncTime.hour).withMinute(syncTime.minute).withSecond(0)
        if (nextRun.isBefore(now)) nextRun = nextRun.plusDays(1)
        return Duration.between(now, nextRun)
    }
}
