package com.personalfinance.sdk.model

import java.time.Instant
import java.util.UUID

enum class AccountType {
    BANK,
    CREDIT_CARD,
    WALLET,
    UNKNOWN,
}

enum class SyncStatus {
    PENDING,
    SYNCED,
}

enum class ExpenseCategory {
    HOUSEHOLD,
    FOOD,
    SHOPPING,
    TRANSPORT,
    ENTERTAINMENT,
    UNKNOWN,
}

data class ParsedTransaction(
    val timestamp: Instant,
    val amount: Double,
    val merchant: String,
    val accountType: AccountType,
    val rawSms: String,
)

data class CategorizationResult(
    val category: ExpenseCategory,
    val confidence: Double,
)

data class TransactionRecord(
    val id: UUID,
    val timestamp: Instant,
    val amount: Double,
    val merchant: String,
    val category: ExpenseCategory,
    val confidence: Double,
    val rawSms: String,
    val syncStatus: SyncStatus,
)
