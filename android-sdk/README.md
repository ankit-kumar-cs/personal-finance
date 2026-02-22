# Personal Finance Android SDK (Single User)

Kotlin-first Android SDK to capture debit SMS, parse deterministic transaction fields, categorize expenses, store offline in Room, and batch sync daily to Google Sheets.

## Android 16 (API 34+) survival notes
- Uses `BroadcastReceiver` for `SMS_RECEIVED` instead of deprecated/background-hostile polling services.
- Uses `WorkManager` periodic work for daily sync so execution is deferred and OS-friendly under Android 13-16 limits.
- Receiver processing is short, async (`goAsync`) and failure-tolerant (silent drop on parse errors).
- Offline-first local persistence means no dependency on network uptime or backend.

## Core package layout
- `receiver/TransactionSmsReceiver`: Captures incoming SMS and routes candidate debit messages to parser/repository.
- `parser/TransactionSmsParser`: Regex-only deterministic extraction of amount/date/merchant/account hints.
- `categorization/ExpenseCategorizer`: Pluggable categorization contract.
- `storage/*`: Room entity/DAO/database.
- `sync/DailySheetSyncWorker`: Daily append-only Google Sheets sync for `PENDING` rows.
- `bridge/FinanceSdkModule`: React Native Native Module interface.

## Exposed React Native methods
- `getPendingCount()`
- `getLastSyncStatus()`
- `updateCategory(transactionId, newCategory)`

## Sheet format (append only)
Columns:
1. `timestamp`
2. `date`
3. `amount`
4. `category`
5. `merchant`
6. `raw_sms`
7. `confidence`
