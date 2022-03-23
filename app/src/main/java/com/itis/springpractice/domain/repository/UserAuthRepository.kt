package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.SignInEntity
import com.itis.springpractice.domain.entity.SignUpEntity

interface UserAuthRepository {
    suspend fun login(email: String, password: String): SignInEntity

    suspend fun register(email: String, password: String): SignUpEntity
}
