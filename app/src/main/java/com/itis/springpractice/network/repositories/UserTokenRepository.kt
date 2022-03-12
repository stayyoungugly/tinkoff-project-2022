package com.itis.springpractice.network.repositories

import com.itis.springpractice.network.api.FirebaseTokenApi

class UserTokenRepository(private var api: FirebaseTokenApi) {

    companion object {
        private const val type = "refresh_token"
    }

    suspend fun saveToken(idToken: String) {
        // Работа с БД
    }

    suspend fun getToken(): String {
        // Работа с БД
        return "todo"
    }

    suspend fun getRefreshToken(): String {
        // Работа с БД
        return "todo"
    }

    suspend fun saveRefreshToken(refreshToken: String): String {
        // Работа с БД
        return "todo"
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
