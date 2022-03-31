package com.itis.springpractice.domain.entity

sealed class VerificationResult
data class VerificationSuccess(
    val email: String,
    val localId: String,
    val passwordHash: String,
    val emailVerified: Boolean,
    val displayName: String,
    val federatedId: String,
    val providerId: String
) : VerificationResult()

data class VerificationError(val reason: String) : VerificationResult()
