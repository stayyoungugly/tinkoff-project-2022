package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.SignInResponse
import com.itis.springpractice.domain.entity.*
import retrofit2.Response

class SignInMapper {
    fun mapToSignIn(response: Result<SignInResponse>): SignInResult {
        response.fold(onSuccess = {
            return SignInSuccess(
                email = it.email,
                idToken = it.idToken,
                localId = it.localId,
                displayName = it.displayName,
                refreshToken = it.refreshToken,
                expiresIn = it.expiresIn,
                registered = it.registered
            )
        }, onFailure = {
            return SignInError(it.message)
        })
    }
}
