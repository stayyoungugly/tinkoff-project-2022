package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.repository.UserRepository

class UserRepositoryImpl(
    private var firestore: Firestore
): UserRepository {

    override suspend fun addUser(user: User) {
        firestore.addUser(user)
    }

    override suspend fun getUserByNickname(nickname: String): User? {
        return firestore.getUserByNickname(nickname)
    }
}
