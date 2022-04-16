package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.AuthErrorResponse
import com.itis.springpractice.domain.entity.AuthErrorEntity
import retrofit2.Response

class ErrorMapper {
    fun mapToErrorEntity(response: Response<AuthErrorResponse>): AuthErrorEntity {
        return if (response.isSuccessful) {
            AuthErrorEntity(
                code = 200,
                message = "OK"
            )
        } else {
            val body = requireNotNull(response.errorBody())
            val authErrorResponse: AuthErrorResponse =
                Gson().fromJson(body.string(), AuthErrorResponse::class.java)
            AuthErrorEntity(
                code = authErrorResponse.authError.code,
                message = authErrorResponse.authError.message
            )
        }
    }
}
