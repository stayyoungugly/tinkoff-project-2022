package com.itis.springpractice.data.request

data class SendVerificationRequest(
    val requestType: String,
    val idToken: String
)
