package com.personalfinance.sdk.sync

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.personalfinance.sdk.storage.FinanceDatabase
import com.personalfinance.sdk.storage.TransactionEntity

/**
 * Android 13-16 compliant background sync using WorkManager.
 * Avoid foreground services for periodic jobs unless user-visible and time critical.
 */
class DailySheetSyncWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val deps = applicationContext as SyncDependencies
        val dao = deps.database.transactionDao()

        val pending = dao.pending()
        if (pending.isEmpty()) return Result.success()

        val token = deps.authProvider.getAccessToken() ?: return Result.success()

        return runCatching {
            deps.sheetsClient.appendRows(token, pending.map { it.toSheetRow() })
            dao.markSynced(pending.map(TransactionEntity::id))
            deps.syncStatusStore.recordSuccess("Synced ${pending.size} rows")
            Result.success()
        }.getOrElse { err ->
            deps.syncStatusStore.recordFailure(err.message ?: "Unknown sync error")
            Result.retry()
        }
    }
}

interface SyncDependencies {
    val database: FinanceDatabase
    val authProvider: GoogleAuthProvider
    val sheetsClient: GoogleSheetsClient
    val syncStatusStore: SyncStatusStore
}

interface GoogleAuthProvider {
    suspend fun getAccessToken(): String?
}

interface GoogleSheetsClient {
    suspend fun appendRows(accessToken: String, rows: List<List<Any>>)
}

interface SyncStatusStore {
    suspend fun recordSuccess(message: String)
    suspend fun recordFailure(message: String)
    suspend fun lastStatus(): String
}

private fun TransactionEntity.toSheetRow(): List<Any> = listOf(
    timestamp.toString(),
    timestamp.toString().substring(0, 10),
    amount,
    category.name.lowercase(),
    merchant,
    rawSms,
    confidence,
)
