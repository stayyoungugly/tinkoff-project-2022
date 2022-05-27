package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.mapper.UserEntityMapper
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.repository.UserRepository

class UserRepositoryImpl(
    private var firestore: Firestore,
    private var userEntityMapper: UserEntityMapper,
    private var preferenceManager: PreferenceManager
) : UserRepository {

    override suspend fun addUser(user: User) {
        firestore.addUser(userEntityMapper.mapToUser(user))
        preferenceManager.saveNickname(user.nickname)
    }

    override suspend fun getUserByNickname(nickname: String): User? {
        val user = firestore.getUserByNickname(nickname)
        val userEntity = user?.let {
            userEntityMapper.mapToUserEntity(it)
        }
        return userEntity
    }
}
