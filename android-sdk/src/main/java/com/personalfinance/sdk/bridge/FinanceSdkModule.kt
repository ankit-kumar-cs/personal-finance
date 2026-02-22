package com.personalfinance.sdk.bridge

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.personalfinance.sdk.data.TransactionRepository
import com.personalfinance.sdk.sync.SyncStatusStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * React Native bridge via classic Native Modules (not TurboModules).
 * Keep API minimal to reduce surface area and maintenance cost.
 */
class FinanceSdkModule(
    reactContext: ReactApplicationContext,
    private val repository: TransactionRepository,
    private val syncStatusStore: SyncStatusStore,
) : ReactContextBaseJavaModule(reactContext) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun getName(): String = "FinanceSdkModule"

    @ReactMethod
    fun getPendingCount(promise: Promise) {
        scope.launch {
            runCatching { repository.getPendingCount() }
                .onSuccess { promise.resolve(it) }
                .onFailure { promise.reject("PENDING_COUNT_ERROR", it) }
        }
    }

    @ReactMethod
    fun getLastSyncStatus(promise: Promise) {
        scope.launch {
            runCatching { syncStatusStore.lastStatus() }
                .onSuccess { promise.resolve(it) }
                .onFailure { promise.reject("LAST_SYNC_STATUS_ERROR", it) }
        }
    }

    @ReactMethod
    fun updateCategory(transactionId: String, newCategory: String, promise: Promise) {
        scope.launch {
            runCatching { repository.updateCategory(transactionId, newCategory) }
                .onSuccess { promise.resolve(true) }
                .onFailure { promise.reject("UPDATE_CATEGORY_ERROR", it) }
        }
    }
}
