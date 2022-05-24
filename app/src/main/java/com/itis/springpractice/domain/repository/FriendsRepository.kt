package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.User

interface FriendsRepository {

    suspend fun addFriend(nickname: String)

    suspend fun getAllFriendsByNickname(): List<User>
}
