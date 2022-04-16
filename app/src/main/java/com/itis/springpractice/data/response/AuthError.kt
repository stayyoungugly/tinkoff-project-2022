package com.itis.springpractice.data.response

import com.google.gson.annotations.SerializedName

data class AuthError(
    @SerializedName("code")
    val code: Int,
    @SerializedName("errors")
    val errors: List<SingleError>,
    @SerializedName("message")
    val message: String
)
