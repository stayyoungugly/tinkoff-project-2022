package com.itis.springpractice.domain.entity

data class TokenEntity(
    val expiresIn: String? = null,
    val idToken: String? = null,
    val projectId: String? = null,
    val refreshToken: String? = null,
    val tokenType: String? = null,
    val userId: String? = null,
    val errorMessage: String? = null
)
