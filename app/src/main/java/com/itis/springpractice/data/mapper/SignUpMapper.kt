package com.itis.springpractice.data.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.SignUpResponse
import com.itis.springpractice.domain.entity.SignUpError
import com.itis.springpractice.domain.entity.SignUpResult
import com.itis.springpractice.domain.entity.SignUpSuccess
import retrofit2.Response

class SignUpMapper {
    fun mapToSignUp(response: Response<SignUpResponse>): SignUpResult {
        return if (response.isSuccessful) {
            val body = requireNotNull(response.body())
            SignUpSuccess(
                email = body.email,
                idToken = body.idToken,
                localId = body.localId,
                refreshToken = body.refreshToken,
                expiresIn = body.expiresIn
            )
        } else {
            val body = requireNotNull(response.errorBody())
            val errorResponse: ErrorResponse =
                Gson().fromJson(body.string(), ErrorResponse::class.java)
            return SignUpError(errorResponse.error.message)
        }
    }
}


