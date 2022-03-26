package com.itis.springpractice.data.response


import com.google.gson.annotations.SerializedName

data class User(
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
    @SerializedName("passwordHash")
    val passwordHash: String,
    @SerializedName("passwordUpdatedAt")
    val passwordUpdatedAt: Double,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("providerUserInfo")
    val providerUserInfo: List<ProviderUserInfo>,
    @SerializedName("validSince")
    val validSince: String
)
