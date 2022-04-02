package com.itis.springpractice.data.impl

import com.itis.springpractice.data.api.firebase.FirebaseTokenApi
import com.itis.springpractice.data.api.mapper.TokenMapper
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.domain.entity.TokenResult
import com.itis.springpractice.domain.entity.TokenSuccess
import com.itis.springpractice.domain.repository.UserTokenRepository

class UserTokenRepositoryImpl(
    private var api: FirebaseTokenApi,
    private var mapper: TokenMapper,
    private var preferenceManager: PreferenceManager
) : UserTokenRepository {

    companion object {
        private const val type = "refresh_token"
    }

    override suspend fun saveToken(idToken: String) {
        preferenceManager.storeToken(idToken)
    }

    override suspend fun getToken(): String {
        return preferenceManager.retrieveToken() ?: ""
    }

    override suspend fun saveRefreshToken(refreshToken: String) {
        preferenceManager.storeRefreshToken(refreshToken)
    }

    override suspend fun getRefreshToken(): String {
        return preferenceManager.retrieveRefreshToken() ?: ""
    }

    override suspend fun refreshToken(): TokenResult {
        val response = mapper.mapToken(api.refreshToken(type, getRefreshToken()))
        val token = (response as TokenSuccess).idToken
        val refreshToken = response.refreshToken
        saveToken(token)
        saveRefreshToken(refreshToken)
        return response
    }

    override suspend fun deleteToken() {
        preferenceManager.deleteToken()
    }
}
