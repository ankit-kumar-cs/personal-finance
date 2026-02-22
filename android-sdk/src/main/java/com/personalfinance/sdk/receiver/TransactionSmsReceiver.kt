package com.personalfinance.sdk.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.personalfinance.sdk.data.TransactionRepository
import com.personalfinance.sdk.parser.TransactionSmsParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Android 13-16 notes:
 * - Use SMS_RECEIVED broadcast receiver (manifest-declared) instead of background service polling.
 * - Keep onReceive lightweight and call goAsync() for I/O.
 * - Never crash the process on parse errors; fail silently and return.
 */
class TransactionSmsReceiver : BroadcastReceiver() {
    private val parser = TransactionSmsParser()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val pendingResult = goAsync()
        scope.launch {
            runCatching {
                val repository: TransactionRepository = SdkLocator.repository(context)
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

                messages.forEach { sms ->
                    val parsed = parser.parse(sms.messageBody) ?: return@forEach
                    repository.ingest(parsed)
                }
            }
            pendingResult.finish()
        }
    }
}

object SdkLocator {
    fun repository(context: Context): TransactionRepository {
        return (context.applicationContext as FinanceSdkDependencies).repository
    }
}

interface FinanceSdkDependencies {
    val repository: TransactionRepository
}
