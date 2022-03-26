package com.itis.springpractice.domain.entity

import com.google.gson.annotations.SerializedName

data class VerificationEntity(
    val email: String? = null,
    val localId: String? = null,
    val passwordHash: String? = null,
    val emailVerified: Boolean? = null,
    val displayName: String? = null,
    val errorMessage: String? = null,
    val federatedId: String? = null,
    val providerId: String? = null
)
