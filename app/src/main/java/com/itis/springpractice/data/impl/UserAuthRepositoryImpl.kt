package com.itis.springpractice.data.impl

import com.itis.springpractice.data.api.firebase.FirebaseAuthApi
import com.itis.springpractice.data.mapper.ErrorMapper
import com.itis.springpractice.data.mapper.SignInMapper
import com.itis.springpractice.data.mapper.SignUpMapper
import com.itis.springpractice.data.mapper.UserInfoMapper
import com.itis.springpractice.data.request.SendVerificationRequest
import com.itis.springpractice.data.request.SignInRequest
import com.itis.springpractice.data.request.SignUpRequest
import com.itis.springpractice.data.request.TokenIdRequest
import com.itis.springpractice.domain.entity.ErrorEntity
import com.itis.springpractice.domain.entity.SignInResult
import com.itis.springpractice.domain.entity.SignUpResult
import com.itis.springpractice.domain.entity.UserInfoResult
import com.itis.springpractice.domain.repository.UserAuthRepository

class UserAuthRepositoryImpl(
    private var api: FirebaseAuthApi,
    private var registerMapper: SignUpMapper,
    private var loginMapper: SignInMapper,
    private var errorMapper: ErrorMapper,
    private var userInfoMapper: UserInfoMapper
) : UserAuthRepository {
    companion object {
        private const val requestField = "VERIFY_EMAIL"
    }

    override suspend fun login(email: String, password: String): SignInResult {
        return loginMapper.mapToSignIn(
            api.login(
                createSignInRequest(
                    email,
                    password
                )
            )
        )
    }

    override suspend fun register(email: String, password: String): SignUpResult {
        return registerMapper.mapToSignUp(
            api.register(
                createSignUpRequest(
                    email,
                    password
                )
            )
        )
    }

    override suspend fun sendVerification(idToken: String): ErrorEntity {
        return errorMapper.mapToErrorEntity(
            api.sendVerification(
                createSendVerificationRequest(idToken)
            )
        )
    }

    override suspend fun getUserInfo(idToken: String): UserInfoResult {
        return userInfoMapper.mapToVerificationEntity(
            api.getUserInfo(
                createTokenIdRequest(idToken)
            )
        )
    }

    override suspend fun deleteUser(idToken: String): ErrorEntity {
        return errorMapper.mapToErrorEntity(
            api.delete(
                createTokenIdRequest(idToken)
            )
        )
    }


    private fun createSendVerificationRequest(
        token: String
    ): SendVerificationRequest =
        SendVerificationRequest(
            requestType = requestField,
            idToken = token
        )

    private fun createTokenIdRequest(
        token: String
    ): TokenIdRequest =
        TokenIdRequest(
            idToken = token
        )

    private fun createSignInRequest(
        emailField: String,
        passwordField: String,
    ): SignInRequest =
        SignInRequest(
            email = emailField,
            password = passwordField,
        )

    private fun createSignUpRequest(
        emailField: String,
        passwordField: String,
    ): SignUpRequest =
        SignUpRequest(
            email = emailField,
            password = passwordField,
        )
}
