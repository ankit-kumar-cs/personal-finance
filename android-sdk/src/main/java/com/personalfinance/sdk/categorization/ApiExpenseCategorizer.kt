package com.personalfinance.sdk.categorization

import com.personalfinance.sdk.model.CategorizationResult
import com.personalfinance.sdk.model.ExpenseCategory
import com.personalfinance.sdk.model.ParsedTransaction

/**
 * API-backed categorizer. Keep payload minimal to preserve privacy.
 * Only sends merchant + amount + coarse account type; never full SMS where possible.
 */
class ApiExpenseCategorizer(
    private val client: CategorizationApiClient,
) : ExpenseCategorizer {
    override suspend fun categorize(transaction: ParsedTransaction): CategorizationResult {
        val response = client.classify(
            merchant = transaction.merchant,
            amount = transaction.amount,
            accountType = transaction.accountType.name,
        )

        val mapped = runCatching { ExpenseCategory.valueOf(response.category.uppercase()) }
            .getOrElse { ExpenseCategory.UNKNOWN }

        return CategorizationResult(
            category = mapped,
            confidence = response.confidence.coerceIn(0.0, 1.0),
        )
    }
}

data class CategoryResponse(
    val category: String,
    val confidence: Double,
)

interface CategorizationApiClient {
    suspend fun classify(merchant: String, amount: Double, accountType: String): CategoryResponse
}
