package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.TokenEntity

interface UserTokenRepository {
    suspend fun saveToken(idToken: String)

    suspend fun getToken(): String

    suspend fun getRefreshToken(): String

    suspend fun saveRefreshToken(refreshToken: String)

    suspend fun refreshToken(): TokenEntity
}
