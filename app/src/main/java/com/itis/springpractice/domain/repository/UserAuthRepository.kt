package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.ErrorEntity
import com.itis.springpractice.domain.entity.SignInEntity
import com.itis.springpractice.domain.entity.SignUpEntity
import com.itis.springpractice.domain.entity.VerificationEntity

interface UserAuthRepository {
    suspend fun login(email: String, password: String): SignInEntity

    suspend fun register(email: String, password: String): SignUpEntity

    suspend fun sendVerification(idToken: String): ErrorEntity

    suspend fun acceptVerification(idToken: String): VerificationEntity

    suspend fun deleteUser(idToken: String): ErrorEntity
}
