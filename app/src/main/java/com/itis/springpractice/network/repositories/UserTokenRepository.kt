package com.itis.springpractice.network.repositories

import com.itis.springpractice.network.api.FirebaseTokenApi

class UserTokenRepository(private var api: FirebaseTokenApi) {

    companion object {
        private const val type = "refresh_token"
    }

    suspend fun saveToken(idToken: String) {
        //  TODO(): Nothing
    }

    suspend fun getToken(): String {
        TODO()
    }

    suspend fun getRefreshToken(): String {
        // Работа с БД
        TODO()
    }

    suspend fun saveRefreshToken(refreshToken: String): String {
        TODO()
    }

    suspend fun refreshToken(): String {
        val response = api.refreshToken(type, getRefreshToken())
        val token = response.idToken
        val refreshToken = response.refreshToken

        saveToken(token)
        saveRefreshToken(refreshToken)

        return token
    }
}
