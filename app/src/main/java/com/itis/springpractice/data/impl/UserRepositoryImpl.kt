package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.mapper.UserEntityMapper
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.repository.UserRepository

class UserRepositoryImpl(
    private val firestore: Firestore,
    private val userEntityMapper: UserEntityMapper,
    private val preferenceManager: PreferenceManager
) : UserRepository {

    companion object {
        private const val DEFAULT_VALUE = ""
    }

    override suspend fun addUser(user: User) {
        firestore.addUser(userEntityMapper.mapToUserResponse(user))
        preferenceManager.saveNickname(user.nickname)
    }

    override suspend fun getUserByNickname(nickname: String): User? {
        val userResponse = firestore.getUserByNickname(nickname)
        return userResponse?.let {
            userEntityMapper.mapToUser(it)
        }
    }

    override suspend fun getUserNickname(): String {
        return preferenceManager.getNickname() ?: DEFAULT_VALUE
    }

    override suspend fun addUserLike(nickname: String, uri: String) {
        firestore.addLike(nickname, uri)
    }

    override suspend fun isPlaceLiked(nickname: String, uri: String): Boolean {
        return (!firestore.isPlaceLiked(nickname, uri)?.uri.isNullOrEmpty())
    }

    override suspend fun deleteUserLike(nickname: String, uri: String) {
        firestore.deleteLike(nickname, uri)
    }

    override suspend fun deleteNickname() {
        preferenceManager.deleteNickname()
    }

}
