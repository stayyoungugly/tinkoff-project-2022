package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.User

interface UserRepository {
    suspend fun addUser(user: User, email: String)

    suspend fun getUserByNickname(nickname: String): User?

    suspend fun deleteNickname()

    suspend fun getUserNickname(): String

    suspend fun addUserLike(nickname: String, uri: String)

    suspend fun deleteUserLike(nickname: String, uri: String)

    suspend fun isPlaceLiked(nickname: String, uri: String): String?

    suspend fun getNumberOf(nickname: String): HashMap<String, Int>

    suspend fun updateUser(firstName: String, lastName: String, uploadAvatar: ByteArray)

    suspend fun updateNickname(email: String)
}
