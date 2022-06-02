package com.itis.springpractice.data.response

data class ReviewResponse(
    var authorNickname: String? = "",
    var textReview: String? = "",
    var rating: Float? = null,
    var created: String? = null
)
