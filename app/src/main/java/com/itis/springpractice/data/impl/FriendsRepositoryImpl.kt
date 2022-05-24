package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.mapper.UserEntityMapper
import com.itis.springpractice.domain.entity.UserEntity
import com.itis.springpractice.domain.repository.FriendsRepository

class FriendsRepositoryImpl(
    private var firestore: Firestore,
    private var userEntityMapper: UserEntityMapper,
    private var preferenceManager: PreferenceManager
) : FriendsRepository {

    companion object {
        private const val DEFAULT_VALUE = ""
    }

    private val userNickname = preferenceManager.getNickname() ?: DEFAULT_VALUE
    override suspend fun addFriend(nickname: String) {
        firestore.addFriend(userNickname, nickname)
    }

    override suspend fun getAllFriendsByNickname(): List<UserEntity> {
        val users = firestore.getFriends(userNickname)
        val userEntities: List<UserEntity?> = users.map {
            it?.let { user ->
                userEntityMapper.mapToUserEntity(user)
            }
        }
        return userEntities.filterNotNull()
    }
}
