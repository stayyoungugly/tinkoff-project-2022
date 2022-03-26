package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.SignInResponse
import com.itis.springpractice.domain.entity.SignInEntity
import com.itis.springpractice.domain.entity.SignUpEntity
import retrofit2.Response

class SignInMapper {
    fun mapToSignInEntity(response: Response<SignInResponse>): SignInEntity {
        return if (response.isSuccessful) {
            SignInEntity(
                email = response.body()?.email,
                idToken = response.body()?.idToken,
                localId = response.body()?.localId,
                displayName = response.body()?.displayName,
                refreshToken = response.body()?.refreshToken,
                expiresIn = response.body()?.expiresIn,
                registered = response.body()?.registered
            )
        } else {
            val errorResponse: ErrorResponse =
                Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
            return SignInEntity(
                errorMessage = errorResponse.error.message
            )
        }
    }
}
