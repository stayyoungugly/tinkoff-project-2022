package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.UserEntity

interface UserRepository {
    suspend fun addUser(user: UserEntity)

    suspend fun getUserByNickname(nickname: String): UserEntity?

}
