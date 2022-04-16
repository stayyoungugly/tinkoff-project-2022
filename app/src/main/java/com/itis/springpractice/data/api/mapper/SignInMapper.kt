package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.AuthErrorResponse
import com.itis.springpractice.data.response.SignInResponse
import com.itis.springpractice.domain.entity.SignInError
import com.itis.springpractice.domain.entity.SignInResult
import com.itis.springpractice.domain.entity.SignInSuccess
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
            val authErrorResponse: AuthErrorResponse =
                Gson().fromJson(body.string(), AuthErrorResponse::class.java)
            return SignInError(authErrorResponse.authError.message)
        }
    }
}
