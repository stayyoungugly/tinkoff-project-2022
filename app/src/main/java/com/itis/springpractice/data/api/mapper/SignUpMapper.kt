package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.SignUpResponse
import com.itis.springpractice.domain.entity.*
import okhttp3.internal.notifyAll
import retrofit2.Response

class SignUpMapper {
    fun mapToSignUp(response: Response<SignUpResponse>): SignUpResult {
        return if (response.isSuccessful)
            SignUpSuccess(
                email = response.body()?.email.toString(),
                idToken = response.body()?.idToken.toString(),
                localId = response.body()?.localId.toString(),
                refreshToken = response.body()?.refreshToken.toString(),
                expiresIn = response.body()?.expiresIn.toString(),
            )
        else {
            val errorResponse: ErrorResponse =
                Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
            return SignUpError(errorResponse.error.message)
        }
    }
}
