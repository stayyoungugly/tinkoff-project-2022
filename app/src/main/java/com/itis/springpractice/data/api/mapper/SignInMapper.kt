package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.SignInResponse
import com.itis.springpractice.domain.entity.*
import retrofit2.Response

class SignInMapper {
    fun mapToSignIn(response: Response<SignInResponse>): SignInResult {
        return if (response.isSuccessful) {
            val body = requireNotNull(response.body())
             SignInSuccess(
                email = body.email,
                idToken = body.idToken,
                localId = body.localId,
                displayName = body.displayName,
                refreshToken = body.refreshToken,
                expiresIn = body.expiresIn,
                registered = body.registered
            )
        } else {
            val body = requireNotNull(response.errorBody())
            val errorResponse: ErrorResponse =
                Gson().fromJson(body.string(), ErrorResponse::class.java)
            return SignInError(errorResponse.error.message)
        }
    }
}
