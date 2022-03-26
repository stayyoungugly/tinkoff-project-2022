package com.itis.springpractice.data.response


import com.google.gson.annotations.SerializedName

data class ProviderUserInfo(
    @SerializedName("federatedId")
    val federatedId: String,
    @SerializedName("providerId")
    val providerId: String
)
