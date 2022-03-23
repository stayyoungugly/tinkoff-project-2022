package com.itis.springpractice.data.response

import com.google.gson.annotations.SerializedName

data class Error(
    @SerializedName("domain")
    val domain: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("reason")
    val reason: String
)
