package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.repository.FriendsRepository

class FriendsRepositoryImpl(
    private var firestore: Firestore,
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
        return firestore.getFriends(userNickname)
    }
}
