package com.itis.springpractice.data.response

import com.google.firebase.storage.StorageReference

data class UserResponse(
    var firstName: String? = "",
    var lastName: String? = "",
    var nickname: String? = "",
    val uploadAvatar: ByteArray? = null,
    val downloadAvatar: StorageReference? = null
)
