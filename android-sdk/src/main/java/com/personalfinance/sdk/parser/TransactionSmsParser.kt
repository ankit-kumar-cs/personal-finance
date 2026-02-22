package com.personalfinance.sdk.parser

import com.personalfinance.sdk.model.AccountType
import com.personalfinance.sdk.model.ParsedTransaction
import java.time.Instant
import java.util.Locale

/**
 * Deterministic regex-based parser only. No AI usage by design.
 */
class TransactionSmsParser {
    private val debitKeywords = listOf("debited", "spent", "purchase", "txn", "withdrawn")
    private val ignoreKeywords = listOf("otp", "one time password", "promo", "offer", "loan", "cashback")

    private val amountRegex = Regex("(?:rs\\.?|inr|mrp|usd)?\\s*([0-9]{1,3}(?:,[0-9]{3})*(?:\\.[0-9]{1,2})?)", RegexOption.IGNORE_CASE)
    private val merchantRegex = Regex("(?:at|to|from)\\s+([A-Za-z0-9 .&*_-]{3,40})", RegexOption.IGNORE_CASE)
    private val accountRegex = Regex("(?:a/c|acct|card|wallet)[^A-Za-z0-9]*([A-Za-z ]+)", RegexOption.IGNORE_CASE)

    fun parse(rawSms: String, receivedAt: Instant = Instant.now()): ParsedTransaction? {
        val normalized = rawSms.lowercase(Locale.US)

        if (ignoreKeywords.any { normalized.contains(it) }) return null
        if (debitKeywords.none { normalized.contains(it) }) return null

        val amount = amountRegex.find(rawSms)
            ?.groups
            ?.get(1)
            ?.value
            ?.replace(",", "")
            ?.toDoubleOrNull()
            ?: return null

        val merchant = merchantRegex.find(rawSms)
            ?.groups
            ?.get(1)
            ?.value
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: "UNKNOWN"

        val accountType = when {
            normalized.contains("card") || normalized.contains("credit") -> AccountType.CREDIT_CARD
            normalized.contains("wallet") || normalized.contains("upi") -> AccountType.WALLET
            normalized.contains("a/c") || normalized.contains("acct") || normalized.contains("bank") -> AccountType.BANK
            else -> AccountType.UNKNOWN
        }

        return ParsedTransaction(
            timestamp = receivedAt,
            amount = amount,
            merchant = merchant,
            accountType = accountType,
            rawSms = rawSms,
        )
    }
}
