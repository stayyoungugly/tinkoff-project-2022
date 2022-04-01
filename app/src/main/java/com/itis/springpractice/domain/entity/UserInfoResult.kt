package com.itis.springpractice.domain.entity

sealed class UserInfoResult

data class UserInfoSuccess(
    val email: String,
    val localId: String,
    val emailVerified: Boolean,
    val createdAt: String,
) : UserInfoResult()

data class UserInfoError(val reason: String) : UserInfoResult()
