package com.itis.springpractice.data.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("error")
    val error: Error
)
