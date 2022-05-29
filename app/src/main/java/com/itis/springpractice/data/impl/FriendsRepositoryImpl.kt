package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.mapper.UserModelMapper
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.repository.FriendsRepository

class FriendsRepositoryImpl(
    private var firestore: Firestore,
    private var userModelMapper: UserModelMapper,
    private var preferenceManager: PreferenceManager
) : FriendsRepository {

    companion object {
        private const val DEFAULT_VALUE = ""
    }

    private val userNickname = preferenceManager.getNickname() ?: DEFAULT_VALUE

    override suspend fun addFriend(nickname: String) {
        firestore.addFriend(userNickname, nickname)
    }

    override suspend fun getAllFriendsByNickname(): List<User> {
        val users = firestore.getFriends(userNickname)
        val userEntities: List<User?> = users.map {
            it?.let { user ->
                userModelMapper.mapToUser(user)
            }
        }
        return userEntities.filterNotNull()
    }

    override suspend fun isUserFriend(nickname: String): Boolean {
        return firestore.isUserFriend(userNickname, nickname)
    }
}
