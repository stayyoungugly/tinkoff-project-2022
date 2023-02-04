package com.itis.springpractice.domain.entity

data class Review(
    var place: Place? = null,
    val uri: String,
    val author: User,
    val textReview: String,
    val rating: Float,
    val created: String
)
