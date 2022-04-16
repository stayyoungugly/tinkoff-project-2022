package com.itis.springpractice.data.response

import com.google.gson.annotations.SerializedName

data class AuthErrorResponse(
    @SerializedName("error")
    val authError: AuthError
)
