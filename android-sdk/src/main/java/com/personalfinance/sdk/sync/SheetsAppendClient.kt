package com.personalfinance.sdk.sync

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * Append-only Google Sheets writer.
 * Writes to a single user-selected spreadsheet ID and sheet tab.
 */
class SheetsAppendClient(
    private val httpClient: OkHttpClient,
    private val spreadsheetId: String,
    private val sheetName: String,
) : GoogleSheetsClient {

    override suspend fun appendRows(accessToken: String, rows: List<List<Any>>) = withContext(Dispatchers.IO) {
        if (rows.isEmpty()) return@withContext

        val values = JSONArray().apply {
            rows.forEach { row ->
                put(JSONArray(row))
            }
        }

        val body = JSONObject()
            .put("majorDimension", "ROWS")
            .put("values", values)
            .toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://sheets.googleapis.com/v4/spreadsheets/$spreadsheetId/values/$sheetName:append?valueInputOption=USER_ENTERED&insertDataOption=INSERT_ROWS")
            .addHeader("Authorization", "Bearer $accessToken")
            .post(body)
            .build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IllegalStateException("Sheets append failed: HTTP ${response.code}")
            }
        }
    }
}
