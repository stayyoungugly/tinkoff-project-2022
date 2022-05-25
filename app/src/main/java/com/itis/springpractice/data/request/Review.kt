package com.itis.springpractice.data.request

data class Review(
    var authorNickname: String? = "",
    var textReview: String? = "",
    var rating: Int? = null
)
