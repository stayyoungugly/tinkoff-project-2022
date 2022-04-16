package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.AuthErrorEntity
import com.itis.springpractice.domain.entity.SignInResult
import com.itis.springpractice.domain.entity.SignUpResult
import com.itis.springpractice.domain.entity.UserInfoResult

interface UserAuthRepository {
    suspend fun login(email: String, password: String): SignInResult

    suspend fun register(email: String, password: String): SignUpResult

    suspend fun sendVerification(idToken: String): AuthErrorEntity

    suspend fun getUserInfo(idToken: String): UserInfoResult

    suspend fun deleteUser(idToken: String): AuthErrorEntity
}
