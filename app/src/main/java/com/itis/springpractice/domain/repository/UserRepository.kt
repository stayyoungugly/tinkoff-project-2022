package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.User

interface UserRepository {
    suspend fun addUser(user: User)

    suspend fun getUserByNickname(nickname: String): User?

    suspend fun getUserNickname(): String

    suspend fun deleteNickname()

    suspend fun getNumberOf(nickname: String): HashMap<String, Int>

    suspend fun updateUser(firstName: String, lastName: String, uploadAvatar: ByteArray)
}
