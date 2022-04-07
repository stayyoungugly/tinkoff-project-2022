package com.itis.springpractice.data.request

data class SignUpRequest(
    val email: String,
    val password: String,
    val returnSecureToken: String = "true"
)
