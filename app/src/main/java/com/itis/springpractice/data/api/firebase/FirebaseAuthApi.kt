package com.itis.springpractice.data.api.firebase

import com.itis.springpractice.data.request.AcceptVerificationRequest
import com.itis.springpractice.data.request.SendVerificationRequest
import com.itis.springpractice.data.request.SignInRequest
import com.itis.springpractice.data.request.SignUpRequest
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.SignInResponse
import com.itis.springpractice.data.response.SignUpResponse
import com.itis.springpractice.data.response.VerificationResponse
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

    @Headers("Content-type: application/json")
    @POST("./accounts:sendOobCode")
    suspend fun sendVerification(
        @Body sendVerificationRequest: SendVerificationRequest
    ): Response<ErrorResponse>

    @Headers("Content-type: application/json")
    @POST("./accounts:lookup")
    suspend fun acceptVerification(
        @Body acceptVerificationRequest: AcceptVerificationRequest
    ): Response<VerificationResponse>

    @Headers("Content-type: application/json")
    @POST("./accounts:delete")
    suspend fun delete(
        @Body acceptVerificationRequest: AcceptVerificationRequest
    ): Response<ErrorResponse>
}
