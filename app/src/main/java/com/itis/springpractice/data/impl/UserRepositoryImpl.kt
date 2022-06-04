package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.mapper.UserModelMapper
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.repository.UserRepository

class UserRepositoryImpl(
    private val firestore: Firestore,
    private val userModelMapper: UserModelMapper,
    private val preferenceManager: PreferenceManager
) : UserRepository {

    companion object {
        private const val DEFAULT_VALUE = ""
    }

    override suspend fun addUser(user: User) {
        firestore.addUser(userModelMapper.mapToUserResponse(user))
        preferenceManager.saveNickname(user.nickname)
    }

    override suspend fun getUserByNickname(nickname: String): User? {
        val userResponse = firestore.getUserByNickname(nickname)
        return userResponse?.let {
            userModelMapper.mapToUser(it)
        }
    }

    override suspend fun getUserNickname(): String {
        return preferenceManager.getNickname() ?: DEFAULT_VALUE
    }

    override suspend fun deleteNickname() {
        preferenceManager.deleteNickname()
    }

    override suspend fun getNumberOf(nickname: String): HashMap<String, Int> {
        return firestore.getNumberOf(nickname)
    }

    override suspend fun updateUser(user: User) {
        if (user.nickname != preferenceManager.getNickname()) {
            preferenceManager.deleteNickname()
            preferenceManager.saveNickname(user.nickname)
        }
        val userNickname = preferenceManager.getNickname() ?: DEFAULT_VALUE
        firestore.updateUser(userModelMapper.mapToUserResponse(user), userNickname)
    }
}
