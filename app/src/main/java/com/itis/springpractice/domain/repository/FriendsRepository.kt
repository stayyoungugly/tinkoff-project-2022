package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.UserEntity

interface FriendsRepository {

    suspend fun addFriend(nickname: String)

    suspend fun getAllFriendsByNickname(): List<UserEntity>
}
