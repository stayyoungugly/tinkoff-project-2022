package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.*

interface UserAuthRepository {
    suspend fun login(email: String, password: String): SignInResult

    suspend fun register(email: String, password: String): SignUpResult

    suspend fun sendVerification(idToken: String): ErrorEntity

    suspend fun acceptVerification(idToken: String): VerificationResult

    suspend fun deleteUser(idToken: String): ErrorEntity
}
