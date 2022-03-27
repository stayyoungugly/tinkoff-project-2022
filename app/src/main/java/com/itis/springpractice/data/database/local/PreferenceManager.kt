package com.itis.springpractice.data.database.local

import android.content.SharedPreferences

class PreferenceManager (
    private var sharedPreferences: SharedPreferences
        ) {

    fun storeToken(idToken: String?) {
        sharedPreferences.edit().putString(TOKEN_PREF, idToken).apply()
    }

    fun retrieveToken(): String? {
        return sharedPreferences.getString(TOKEN_PREF, "")
    }

    fun storeRefreshToken(refreshToken: String?) {
        sharedPreferences.edit().putString(REFRESH_TOKEN_PREF, refreshToken).apply()
    }

    fun retrieveRefreshToken(): String? {
        return sharedPreferences.getString(REFRESH_TOKEN_PREF, "")
    }

    companion object {
        private const val REFRESH_TOKEN_PREF = "refresh_token"
        private const val TOKEN_PREF = "token"
    }
}
