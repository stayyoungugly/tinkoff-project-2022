package com.itis.springpractice.network.api

import com.itis.springpractice.network.responses.TokenResponse
import retrofit2.http.Field
import retrofit2.http.POST

interface FirebaseTokenApi {

    @POST("token")
    suspend fun refreshToken(
        @Field("grant_type") type: String,
        @Field("refresh_token") token: String
    ): TokenResponse

}
