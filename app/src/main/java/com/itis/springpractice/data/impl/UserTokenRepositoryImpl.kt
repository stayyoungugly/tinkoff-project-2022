package com.itis.springpractice.data.impl

import com.itis.springpractice.data.api.firebase.FirebaseTokenApi
import com.itis.springpractice.data.api.mapper.TokenMapper
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.domain.entity.TokenEntity
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

    override suspend fun refreshToken(): TokenEntity {
        val response = mapper.mapTokenEntity(api.refreshToken(type, getRefreshToken()))
        val token = response.idToken
        val refreshToken = response.refreshToken
        if (token != null) {
            saveToken(token)
        }
        if (refreshToken != null) {
            saveRefreshToken(refreshToken)
        }
        return response
    }
}
