package com.itis.springpractice.domain.entity

data class Review(
    val author: User,
    val textReview: String,
    val rating: Float,
    val created: String
)
