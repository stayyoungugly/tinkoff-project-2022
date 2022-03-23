package com.itis.springpractice.data.api.firebase

import com.itis.springpractice.data.request.SignInRequest
import com.itis.springpractice.data.request.SignUpRequest
import com.itis.springpractice.data.response.SignInResponse
import com.itis.springpractice.data.response.SignUpResponse
import retrofit2.Response
import retrofit2.http.*

interface FirebaseAuthApi {

    @Headers("Content-type: application/json")
    @POST("./accounts:signUp")
    suspend fun register(
        @Body signUpRequest: SignUpRequest
    ): Response<SignUpResponse>

    @Headers("Content-type: application/json")
    @POST("./accounts:signInWithPassword")
    suspend fun login(
        @Body signInRequest: SignInRequest
    ): Response<SignInResponse>

}
