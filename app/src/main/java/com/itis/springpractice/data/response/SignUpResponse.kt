package com.itis.springpractice.data.response


import com.google.gson.annotations.SerializedName

data class SignUpResponse(
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
    @SerializedName("error")
    val error: Error
)
