package com.itis.springpractice.data.api.mapper

import com.itis.springpractice.data.response.TokenResponse
import com.itis.springpractice.domain.entity.TokenError
import com.itis.springpractice.domain.entity.TokenResult
import com.itis.springpractice.domain.entity.TokenSuccess

class TokenMapper {
    fun mapToken(response: Result<TokenResponse>): TokenResult {
        response.fold(onSuccess = {
            return TokenSuccess(
                idToken = it.idToken,
                refreshToken = it.refreshToken,
                expiresIn = it.expiresIn,
                projectId = it.projectId,
                tokenType = it.tokenType,
                userId = it.userId
            )
        }, onFailure = {
            return TokenError(it.message)
        })
    }
}
