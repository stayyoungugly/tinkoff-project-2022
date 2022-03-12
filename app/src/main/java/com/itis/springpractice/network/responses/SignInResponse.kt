package com.itis.springpractice.network.responses

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("expiresIn")
    val expiresIn: String,
    @SerializedName("idToken")
    val idToken: String,
    @SerializedName("localId")
    val localId: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("registered")
    val registered: Boolean
)
