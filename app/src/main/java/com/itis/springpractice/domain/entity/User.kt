package com.itis.springpractice.domain.entity

import com.google.firebase.storage.StorageReference

data class User (
    val firstName: String,
    val lastName: String,
    val nickname: String,
    val uploadAvatar: ByteArray?,
    val downloadAvatar: StorageReference?
)
