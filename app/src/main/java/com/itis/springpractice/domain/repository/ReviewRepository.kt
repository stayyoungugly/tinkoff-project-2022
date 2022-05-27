package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.Review

interface ReviewRepository {
    suspend fun getReviewsByPlace(placeURI: String): List<Review?>

    suspend fun addReviewOnPlace(placeURI: String, textReview: String, rating: Int): Boolean
}
