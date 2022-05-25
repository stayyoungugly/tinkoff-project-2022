package com.itis.springpractice.domain.entity

data class ReviewEntity(
    val author: UserEntity,
    val textReview: String,
    val rating: Int
)
