package com.itis.springpractice.domain.entity

data class SignInEntity(
    val displayName: String? = null,
    val email: String? = null,
    val expiresIn: String? = null,
    val idToken: String? = null,
    val localId: String? = null,
    val refreshToken: String? = null,
    val registered: Boolean? = null,
    val errorMessage: String? = null
)
