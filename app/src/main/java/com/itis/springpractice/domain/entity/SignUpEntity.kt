package com.itis.springpractice.domain.entity

data class SignUpEntity(
    val idToken: String? = null,
    val email: String? = null,
    val refreshToken: String? = null,
    val expiresIn: String? = null,
    val localId: String? = null,
    val errorMessage: String? = null
)
