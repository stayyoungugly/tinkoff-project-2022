package com.itis.springpractice.data.response


import com.google.gson.annotations.SerializedName

data class VerificationResponse(
    @SerializedName("users")
    val users: List<User>
)
