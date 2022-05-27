package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.User

interface UserRepository {
    suspend fun addUser(user: User)

    suspend fun getUserByNickname(nickname: String): User?
}
