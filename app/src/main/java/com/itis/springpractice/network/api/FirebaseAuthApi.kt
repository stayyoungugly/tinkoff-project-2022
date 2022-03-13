package com.itis.springpractice.network.api

import com.itis.springpractice.network.responses.SignInResponse
import com.itis.springpractice.network.responses.SignUpResponse
import com.itis.springpractice.network.responses.TokenResponse
import retrofit2.http.*

interface FirebaseAuthApi {

    @FormUrlEncoded
    @POST("accounts:signUp")
    suspend fun register(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("returnSecureToken") secure: String
    ): SignUpResponse

    @FormUrlEncoded
    @POST("accounts:signInWithPassword")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("returnSecureToken") secure: String
    ): SignInResponse

}
