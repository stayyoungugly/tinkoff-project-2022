package com.itis.springpractice.data.impl

import com.itis.springpractice.data.api.firebase.FirebaseAuthApi
import com.itis.springpractice.data.api.mapper.SignInMapper
import com.itis.springpractice.data.api.mapper.SignUpMapper
import com.itis.springpractice.data.request.SignInRequest
import com.itis.springpractice.data.request.SignUpRequest
import com.itis.springpractice.domain.entity.SignInEntity
import com.itis.springpractice.domain.entity.SignUpEntity
import com.itis.springpractice.domain.repository.UserAuthRepository

class UserAuthRepositoryImpl(
    private var api: FirebaseAuthApi,
    private var registerMapper: SignUpMapper,
    private var loginMapper: SignInMapper
) : UserAuthRepository {
    companion object {
        private const val secure = "true"
    }

    override suspend fun login(email: String, password: String): SignInEntity {
        return loginMapper.mapToSignInEntity(
            api.login(
                createSignInRequest(
                    email,
                    password,
                    secure
                )
            )
        )
    }

    override suspend fun register(email: String, password: String): SignUpEntity {
        return registerMapper.mapToSignUpEntity(
            api.register(
                createSignUpRequest(
                    email,
                    password,
                    secure
                )
            )
        )
    }

    private fun createSignInRequest(
        emailField: String,
        passwordField: String,
        secureField: String
    ): SignInRequest =
        SignInRequest(
            email = emailField,
            password = passwordField,
            returnSecureToken = secureField
        )

    private fun createSignUpRequest(
        emailField: String,
        passwordField: String,
        secureField: String
    ): SignUpRequest =
        SignUpRequest(
            email = emailField,
            password = passwordField,
            returnSecureToken = secureField
        )
}
