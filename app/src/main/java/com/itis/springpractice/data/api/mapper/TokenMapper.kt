package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.TokenResponse
import com.itis.springpractice.domain.entity.TokenEntity
import retrofit2.Response

class TokenMapper {
    fun mapTokenEntity(response: Response<TokenResponse>): TokenEntity {
        return if (response.isSuccessful) {
            TokenEntity(
                idToken = response.body()?.idToken,
                refreshToken = response.body()?.refreshToken,
                expiresIn = response.body()?.expiresIn,
                projectId = response.body()?.projectId,
                tokenType = response.body()?.tokenType,
                userId = response.body()?.userId
            )
        } else {
            val errorResponse: ErrorResponse =
                Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
            return TokenEntity(
                errorMessage = errorResponse.error.message
            )
        }
    }
}
