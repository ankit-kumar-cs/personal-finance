package com.personalfinance.sdk.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.personalfinance.sdk.model.ExpenseCategory
import com.personalfinance.sdk.model.SyncStatus
import java.time.Instant
import java.util.UUID

/**
 * Offline-first Room entity.
 * The app can operate indefinitely without network; every record remains locally queryable.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    @ColumnInfo(name = "timestamp")
    val timestamp: Instant,
    @ColumnInfo(name = "amount")
    val amount: Double,
    @ColumnInfo(name = "merchant")
    val merchant: String,
    @ColumnInfo(name = "category")
    val category: ExpenseCategory,
    @ColumnInfo(name = "confidence")
    val confidence: Double,
    @ColumnInfo(name = "raw_sms")
    val rawSms: String,
    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.PENDING,
)
