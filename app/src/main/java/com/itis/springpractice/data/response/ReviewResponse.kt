package com.itis.springpractice.data.response

data class ReviewResponse(
    var authorNickname: String? = "",
    var textReview: String? = "",
    var rating: Int? = null
)
