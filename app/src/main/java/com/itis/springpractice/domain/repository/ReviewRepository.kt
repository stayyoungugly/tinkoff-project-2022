package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.ReviewEntity

interface ReviewRepository {
    suspend fun getReviewsByPlace(placeURI: String): List<ReviewEntity?>

    suspend fun addReviewOnPlace(placeURI: String, textReview: String, rating: Int): Boolean
}
