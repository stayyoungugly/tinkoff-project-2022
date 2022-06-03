package com.itis.springpractice.data.response

data class ReviewResponse(
    var uri: String? = "",
    var authorNickname: String? = "",
    var textReview: String? = "",
    var rating: Float? = null,
    var created: String? = null
)
