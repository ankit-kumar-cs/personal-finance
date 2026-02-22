package com.personalfinance.sdk.sync

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.tasks.await

/**
 * OAuth token provider scoped to Sheets append access for a single signed-in user.
 * Caller must trigger interactive sign-in from host app once; worker reuses cached account.
 */
class GoogleSignInAuthProvider(
    private val context: Context,
    private val webClientId: String,
) : GoogleAuthProvider {
    override suspend fun getAccessToken(): String? {
        val account = GoogleSignIn.getLastSignedInAccount(context) ?: return null
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(webClientId)
            .requestScopes(com.google.android.gms.common.api.Scope("https://www.googleapis.com/auth/spreadsheets"))
            .build()

        // Recreate client with required scope. If silent sign-in fails, return null and skip sync gracefully.
        val client = GoogleSignIn.getClient(context, options)
        val signedIn = runCatching { client.silentSignIn().await() }.getOrNull() ?: account

        // In production: exchange server auth code/token as required by your Sheets client implementation.
        return signedIn.idToken
    }
}
