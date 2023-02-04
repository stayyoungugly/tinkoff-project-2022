package com.itis.springpractice.data.api.firebase

import com.itis.springpractice.data.request.TokenIdRequest
import com.itis.springpractice.data.request.SendVerificationRequest
import com.itis.springpractice.data.request.SignInRequest
import com.itis.springpractice.data.request.SignUpRequest
import com.itis.springpractice.data.response.*
import retrofit2.Response
import retrofit2.http.*

interface FirebaseAuthApi {

    @POST("./accounts:signUp")
    suspend fun register(
        @Body signUpRequest: SignUpRequest
    ): Response<SignUpResponse>

    @POST("./accounts:signInWithPassword")
    suspend fun login(
        @Body signInRequest: SignInRequest
    ): Response<SignInResponse>

    @POST("./accounts:sendOobCode")
    suspend fun sendVerification(
        @Body sendVerificationRequest: SendVerificationRequest
    ): Response<ErrorResponse>

    @POST("./accounts:lookup")
    suspend fun getUserInfo(
        @Body tokenIdRequest: TokenIdRequest
    ): Response<UserInfoResponse>

    @POST("./accounts:delete")
    suspend fun delete(
        @Body tokenIdRequest: TokenIdRequest
    ): Response<ErrorResponse>
}
