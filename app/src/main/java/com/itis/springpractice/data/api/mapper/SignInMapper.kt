package com.itis.springpractice.data.api.mapper

import com.itis.springpractice.data.response.SignInResponse
import com.itis.springpractice.domain.entity.SignInEntity
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
        } else SignInEntity(
            errorMessage = response.message()
        )
    }
}
