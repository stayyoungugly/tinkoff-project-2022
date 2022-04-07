package com.itis.springpractice.data.response

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("expires_in")
    val expiresIn: String,
    @SerializedName("id_token")
    val idToken: String,
    @SerializedName("project_id")
    val projectId: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    @SerializedName("user_id")
    val userId: String
)
