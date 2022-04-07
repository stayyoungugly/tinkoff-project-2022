package com.itis.springpractice.data.response


import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("customAuth")
    val customAuth: Boolean,
    @SerializedName("disabled")
    val disabled: Boolean,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("emailVerified")
    val emailVerified: Boolean,
    @SerializedName("lastLoginAt")
    val lastLoginAt: String,
    @SerializedName("localId")
    val localId: String,
    @SerializedName("passwordUpdatedAt")
    val passwordUpdatedAt: Double,
    @SerializedName("photoUrl")
    val photoUrl: String,
)
