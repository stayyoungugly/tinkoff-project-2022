package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.AuthErrorResponse
import com.itis.springpractice.data.response.TokenResponse
import com.itis.springpractice.domain.entity.TokenError
import com.itis.springpractice.domain.entity.TokenResult
import com.itis.springpractice.domain.entity.TokenSuccess
import retrofit2.Response

class TokenMapper {
    fun mapToken(response: Response<TokenResponse>): TokenResult {
        return if (response.isSuccessful) {
            val body = requireNotNull(response.body())
             TokenSuccess(
                idToken = body.idToken,
                refreshToken = body.refreshToken,
                expiresIn = body.expiresIn,
                projectId = body.projectId,
                tokenType = body.tokenType,
                userId = body.userId
            )
        }else {
            val body = requireNotNull(response.errorBody())
            val authErrorResponse: AuthErrorResponse =
                Gson().fromJson(body.string(), AuthErrorResponse::class.java)
            return TokenError(authErrorResponse.authError.message)
        }
    }
}
