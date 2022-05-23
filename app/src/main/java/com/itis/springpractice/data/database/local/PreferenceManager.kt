package com.itis.springpractice.data.database.local

import android.content.SharedPreferences

class PreferenceManager (
    private var sharedPreferences: SharedPreferences
        ) {
    fun storeToken(idToken: String) {
        sharedPreferences.edit().putString(TOKEN_PREF, idToken).apply()
    }

    fun retrieveToken(): String? {
        return sharedPreferences.getString(TOKEN_PREF, DEFAULT_VALUE)
    }

    fun storeRefreshToken(refreshToken: String) {
        sharedPreferences.edit().putString(REFRESH_TOKEN_PREF, refreshToken).apply()
    }

    fun retrieveRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN_PREF, DEFAULT_VALUE)
    }

    fun deleteToken() {
        sharedPreferences.edit().putString(TOKEN_PREF, DEFAULT_VALUE).apply()
    }

    companion object {
        private const val REFRESH_TOKEN_PREF = "refresh_token"
        private const val TOKEN_PREF = "token"
        private val DEFAULT_VALUE = null
    }
}
