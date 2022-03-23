package com.itis.springpractice.data.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("errors")
    val errors: List<Error>,
    @SerializedName("message")
    val message: String
)
