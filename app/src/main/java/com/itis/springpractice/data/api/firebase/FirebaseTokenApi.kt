package com.itis.springpractice.data.api.firebase

import com.itis.springpractice.data.response.TokenResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface FirebaseTokenApi {
    @FormUrlEncoded
    @POST("token")
    suspend fun refreshToken(
        @Field("grant_type") type: String,
        @Field("refresh_token") token: String
    ): Response<TokenResponse>
}
