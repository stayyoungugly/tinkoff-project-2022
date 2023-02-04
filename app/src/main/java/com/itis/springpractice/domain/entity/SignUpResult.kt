package com.itis.springpractice.domain.entity

sealed class SignUpResult

data class SignUpSuccess(
    val idToken: String,
    val email: String,
    val localId: String,
    val refreshToken: String,
    val expiresIn: String
) : SignUpResult()

data class SignUpError(val reason: String) : SignUpResult()

