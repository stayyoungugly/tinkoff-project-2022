package com.itis.springpractice.domain.entity

data class Review(
    val uri: String,
    val author: User,
    val textReview: String,
    val rating: Float,
    val created: String
)
