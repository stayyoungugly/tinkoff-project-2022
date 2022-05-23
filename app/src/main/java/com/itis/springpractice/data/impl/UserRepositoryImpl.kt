package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.mapper.UserEntityMapper
import com.itis.springpractice.domain.entity.UserEntity
import com.itis.springpractice.domain.repository.UserRepository

class UserRepositoryImpl(
    private var firestore: Firestore,
    private var userEntityMapper: UserEntityMapper
) : UserRepository {

    override suspend fun addUser(user: UserEntity) {
        firestore.addUser(userEntityMapper.mapToUser(user))
    }

    override suspend fun getUserByNickname(nickname: String): UserEntity? {
        val user = firestore.getUserByNickname(nickname)
        val userEntity = user?.let {
            userEntityMapper.mapToUserEntity(it)
        }
        return userEntity
    }
}
