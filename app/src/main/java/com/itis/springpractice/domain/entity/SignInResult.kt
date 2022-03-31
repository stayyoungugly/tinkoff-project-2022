package com.itis.springpractice.domain.entity

sealed class SignInResult
data class SignInSuccess(
    val displayName: String,
    val email: String,
    val expiresIn: String,
    val idToken: String,
    val localId: String,
    val refreshToken: String,
    val registered: Boolean
) : SignInResult()

data class SignInError(val reason: String) : SignInResult()
