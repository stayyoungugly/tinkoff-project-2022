package com.itis.springpractice.data.mapper

import com.itis.springpractice.data.response.UserResponse
import com.itis.springpractice.domain.entity.User

class UserEntityMapper {
    fun mapToUser(userResponse: UserResponse): UserResponse): User? {
        return if (!(userResponse.firstName.isNullOrEmpty()) && (!userResponse.lastName.isNullOrEmpty()) && (!userResponse.nickname.isNullOrEmpty())) {
            User(
                firstName = userResponse.firstName!!,
                lastName = userResponse.lastName!!,
                nickname = userResponse.nickname!!
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
