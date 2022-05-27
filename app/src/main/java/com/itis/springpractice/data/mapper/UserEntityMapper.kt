package com.itis.springpractice.data.mapper

import com.itis.springpractice.data.response.UserResponse
import com.itis.springpractice.domain.entity.User

class UserEntityMapper {
    fun mapToUser(user: UserResponse): User? {
        return if (!(user.firstName.isNullOrEmpty()) && (!user.lastName.isNullOrEmpty()) && (!user.nickname.isNullOrEmpty())) {
            User(
                firstName = user.firstName!!,
                lastName = user.lastName!!,
                nickname = user.nickname!!
            )
        } else null
    }

    fun mapToUserResponse(user: User): UserResponse {
        return UserResponse(
            firstName = user.firstName,
            lastName = user.lastName,
            nickname = user.nickname
        )
    }
}
