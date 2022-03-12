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

    suspend fun refreshToken(): String {
        val token = api.refreshToken(type, getToken()).idToken
        saveToken(token)
        return token
    }
}
