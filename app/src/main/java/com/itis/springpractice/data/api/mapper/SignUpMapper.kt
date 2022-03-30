package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.SignUpResponse
import com.itis.springpractice.domain.entity.*
import retrofit2.Response

class SignUpMapper {
    fun mapToSignUp(response: Result<SignUpResponse>): SignUpResult {
        response.fold(onSuccess = {
            return SignUpSuccess(
                email = it.email,
                idToken = it.idToken,
                localId = it.localId,
                refreshToken = it.refreshToken,
                expiresIn = it.expiresIn,
            )
        }, onFailure = {
            return SignUpError(it.message)
        })
    }
}
