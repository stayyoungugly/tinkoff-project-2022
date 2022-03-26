package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.SignUpResponse
import com.itis.springpractice.domain.entity.SignInEntity
import com.itis.springpractice.domain.entity.SignUpEntity
import retrofit2.Response

class SignUpMapper {
    fun mapToSignUpEntity(response: Response<SignUpResponse>): SignUpEntity {
        return if (response.isSuccessful) {
            SignUpEntity(
                email = response.body()?.email,
                idToken = response.body()?.idToken,
                localId = response.body()?.localId,
                refreshToken = response.body()?.refreshToken,
                expiresIn = response.body()?.expiresIn,
            )
        } else {
            val errorResponse: ErrorResponse =
                Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
            SignUpEntity(
                errorMessage = errorResponse.error.message
            )
        }
    }
}
