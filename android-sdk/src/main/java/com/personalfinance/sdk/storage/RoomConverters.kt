package com.personalfinance.sdk.storage

import androidx.room.TypeConverter
import com.personalfinance.sdk.model.ExpenseCategory
import com.personalfinance.sdk.model.SyncStatus
import java.time.Instant

class RoomConverters {
    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun fromCategory(value: ExpenseCategory?): String? = value?.name

    @TypeConverter
    fun toCategory(value: String?): ExpenseCategory? = value?.let(ExpenseCategory::valueOf)

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus?): String? = value?.name

    @TypeConverter
    fun toSyncStatus(value: String?): SyncStatus? = value?.let(SyncStatus::valueOf)
}
