package com.itis.springpractice.domain.entity

data class User (
    val firstName: String,
    val lastName: String,
    var nickname: String,
    val avatar: ByteArray?
)
