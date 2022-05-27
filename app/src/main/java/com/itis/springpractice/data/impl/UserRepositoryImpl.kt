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
}
