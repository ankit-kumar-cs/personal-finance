package com.personalfinance.sdk.categorization

import com.personalfinance.sdk.model.CategorizationResult
import com.personalfinance.sdk.model.ParsedTransaction

/**
 * Pluggable strategy for category assignment:
 * - ApiExpenseCategorizer for initial rollout
 * - OnDeviceExpenseCategorizer for future private/offline inference
 */
interface ExpenseCategorizer {
    suspend fun categorize(transaction: ParsedTransaction): CategorizationResult
}
