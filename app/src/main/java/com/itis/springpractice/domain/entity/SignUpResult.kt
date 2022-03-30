package com.itis.springpractice.domain.entity

sealed class SignUpResult
data class SignUpSuccess(
    val idToken: String,
    val email: String,
    val refreshToken: String,
    val expiresIn: String,
    val localId: String,
) : SignUpResult()

data class SignUpError(val reason: String?) : SignUpResult()

