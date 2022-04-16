package com.itis.springpractice.data.response


import com.google.gson.annotations.SerializedName

data class PlaceResponse(
    @SerializedName("results")
    val results: List<Result>,
    @SerializedName("status")
    val status: String
)
