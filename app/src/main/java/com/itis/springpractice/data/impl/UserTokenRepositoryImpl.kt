package com.itis.springpractice.data.impl

import com.itis.springpractice.data.api.firebase.FirebaseTokenApi
import com.itis.springpractice.data.api.mapper.TokenMapper
import com.itis.springpractice.domain.entity.TokenEntity
import com.itis.springpractice.domain.repository.UserTokenRepository

class UserTokenRepositoryImpl(
    private var api: FirebaseTokenApi,
    private var mapper: TokenMapper
) : UserTokenRepository {

    companion object {
        private const val type = "refresh_token"
    }

    override suspend fun saveToken(idToken: String) {
        //  TODO(): Nothing
    }

    override suspend fun getToken(): String {
        TODO()
    }

    override suspend fun getRefreshToken(): String {
        // Работа с БД
        TODO()
    }

    override suspend fun saveRefreshToken(refreshToken: String): String {
        TODO()
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
