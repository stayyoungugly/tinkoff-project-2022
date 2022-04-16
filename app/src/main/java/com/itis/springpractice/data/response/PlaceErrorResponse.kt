package com.itis.springpractice.data.response


import com.google.gson.annotations.SerializedName

data class PlaceErrorResponse(
    @SerializedName("error_message")
    val errorMessage: String,
    @SerializedName("status")
    val status: String
)
