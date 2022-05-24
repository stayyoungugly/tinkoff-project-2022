package com.itis.springpractice.data.mapper

import com.itis.springpractice.data.response.User
import com.itis.springpractice.domain.entity.UserEntity

class UserEntityMapper {
    fun mapToUserEntity(user: User): UserEntity? {
        return if (!(user.firstName.isNullOrEmpty()) && (!user.lastName.isNullOrEmpty()) && (!user.nickname.isNullOrEmpty())) {
            UserEntity(
                firstName = user.firstName!!,
                lastName = user.lastName!!,
                nickname = user.nickname!!
            )
        } else null
    }

    fun mapToUser(userEntity: UserEntity): User {
        return User(
            firstName = userEntity.firstName,
            lastName = userEntity.lastName,
            nickname = userEntity.nickname
        )
    }
}
