package com.itis.springpractice.data.impl

import com.itis.springpractice.data.api.firebase.FirebaseAuthApi
import com.itis.springpractice.data.api.mapper.ErrorMapper
import com.itis.springpractice.data.api.mapper.SignInMapper
import com.itis.springpractice.data.api.mapper.SignUpMapper
import com.itis.springpractice.data.api.mapper.VerificationMapper
import com.itis.springpractice.data.request.AcceptVerificationRequest
import com.itis.springpractice.data.request.SendVerificationRequest
import com.itis.springpractice.data.request.SignInRequest
import com.itis.springpractice.data.request.SignUpRequest
import com.itis.springpractice.domain.entity.ErrorEntity
import com.itis.springpractice.domain.entity.SignInEntity
import com.itis.springpractice.domain.entity.SignUpEntity
import com.itis.springpractice.domain.entity.VerificationEntity
import com.itis.springpractice.domain.repository.UserAuthRepository

class UserAuthRepositoryImpl(
    private var api: FirebaseAuthApi,
    private var registerMapper: SignUpMapper,
    private var loginMapper: SignInMapper,
    private var errorMapper: ErrorMapper,
    private var verificationMapper: VerificationMapper
) : UserAuthRepository {
    companion object {
        private const val secure = "true"
        private const val requestField = "VERIFY_EMAIL"
    }

    override suspend fun login(email: String, password: String): SignInEntity {
        return loginMapper.mapToSignInEntity(
            api.login(
                createSignInRequest(
                    email,
                    password
                )
            )
        )
    }

    override suspend fun register(email: String, password: String): SignUpEntity {
        return registerMapper.mapToSignUpEntity(
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

    override suspend fun acceptVerification(idToken: String): VerificationEntity {
        return verificationMapper.mapToVerificationEntity(
            api.acceptVerification(
                createAcceptVerificationRequest(idToken)
            )
        )
    }

    override suspend fun deleteUser(idToken: String): ErrorEntity {
        return errorMapper.mapToErrorEntity(
            api.delete(
                createAcceptVerificationRequest(idToken)
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

    private fun createAcceptVerificationRequest(
        token: String
    ): AcceptVerificationRequest =
        AcceptVerificationRequest(
            idToken = token
        )

    private fun createSignInRequest(
        emailField: String,
        passwordField: String,
    ): SignInRequest =
        SignInRequest(
            email = emailField,
            password = passwordField,
            returnSecureToken = secure
        )

    private fun createSignUpRequest(
        emailField: String,
        passwordField: String,
    ): SignUpRequest =
        SignUpRequest(
            email = emailField,
            password = passwordField,
            returnSecureToken = secure
        )
}
