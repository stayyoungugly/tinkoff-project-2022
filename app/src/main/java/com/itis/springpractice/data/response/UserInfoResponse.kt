package com.itis.springpractice.data.response


import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    @SerializedName("users")
    val userAccounts: List<UserInfo>
)
