package com.itis.springpractice.data.impl

import com.itis.springpractice.data.api.firebase.FirebaseTokenApi
import com.itis.springpractice.data.api.mapper.TokenMapper
import com.itis.springpractice.data.database.token.TokenDao
import com.itis.springpractice.data.database.token.model.Token
import com.itis.springpractice.domain.entity.TokenEntity
import com.itis.springpractice.domain.repository.UserTokenRepository

class UserTokenRepositoryImpl(
    private var api: FirebaseTokenApi,
    private var mapper: TokenMapper,
    private var tokenDao: TokenDao
) : UserTokenRepository {

    companion object {
        private const val type = "refresh_token"
    }

    override suspend fun saveToken(idToken: String) {
        tokenDao.saveToken(idToken)
    }

    override suspend fun getToken(): String {
        return tokenDao.findAllTokens()[0].idToken
    }

    override suspend fun getRefreshToken(): String {
        return tokenDao.findAllTokens()[0].refreshToken
    }

    override suspend fun saveRefreshToken(refreshToken: String): String {
        tokenDao.saveRefreshToken(refreshToken)
        return tokenDao.findAllTokens()[0].refreshToken
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
