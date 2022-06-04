package com.itis.springpractice.data.mapper

import com.itis.springpractice.data.response.UserFirestore
import com.itis.springpractice.data.response.UserResponse
import com.itis.springpractice.domain.entity.User

class UserModelMapper {
    fun mapToUser(userFirestore: UserFirestore, avatar: ByteArray?): User? {
        return if (!(userFirestore.firstName.isNullOrEmpty()) && (!userFirestore.lastName.isNullOrEmpty()) && (!userFirestore.nickname.isNullOrEmpty())) {
            User(
                firstName = userFirestore.firstName!!,
                lastName = userFirestore.lastName!!,
                nickname = userFirestore.nickname!!,
                avatar = avatar
            )
        } else null
    }

    fun mapToUserResponse(user: User): UserResponse {
        return UserResponse(
            firstName = user.firstName,
            lastName = user.lastName,
            nickname = user.nickname,
            avatar = user.avatar
        )
    }
}
