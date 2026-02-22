package com.personalfinance.sdk.data

import com.personalfinance.sdk.categorization.ExpenseCategorizer
import com.personalfinance.sdk.model.ExpenseCategory
import com.personalfinance.sdk.model.ParsedTransaction
import com.personalfinance.sdk.model.SyncStatus
import com.personalfinance.sdk.storage.TransactionDao
import com.personalfinance.sdk.storage.TransactionEntity

class TransactionRepository(
    private val dao: TransactionDao,
    private val categorizer: ExpenseCategorizer,
) {
    suspend fun ingest(parsed: ParsedTransaction) {
        val categorization = runCatching { categorizer.categorize(parsed) }
            .getOrElse {
                // Fail closed to UNKNOWN; ingestion must never block on categorization errors.
                com.personalfinance.sdk.model.CategorizationResult(ExpenseCategory.UNKNOWN, 0.0)
            }

        dao.insert(
            TransactionEntity(
                timestamp = parsed.timestamp,
                amount = parsed.amount,
                merchant = parsed.merchant,
                category = categorization.category,
                confidence = categorization.confidence,
                rawSms = parsed.rawSms,
                syncStatus = SyncStatus.PENDING,
            ),
        )
    }

    suspend fun getPendingCount(): Int = dao.pendingCount()

    suspend fun updateCategory(transactionId: String, newCategory: String) {
        dao.updateCategory(transactionId, newCategory)
    }
}
