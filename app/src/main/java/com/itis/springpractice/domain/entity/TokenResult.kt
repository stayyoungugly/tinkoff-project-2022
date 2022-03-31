package com.itis.springpractice.domain.entity

sealed class TokenResult
data class TokenSuccess(
    val expiresIn: String,
    val idToken: String,
    val projectId: String,
    val refreshToken: String,
    val tokenType: String,
    val userId: String,
): TokenResult()
data class TokenError(val reason: String): TokenResult()
