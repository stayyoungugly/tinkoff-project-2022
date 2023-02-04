package com.itis.springpractice.data.request

data class SignInRequest(
    val email: String,
    val password: String,
    val returnSecureToken: String = "true"
)
