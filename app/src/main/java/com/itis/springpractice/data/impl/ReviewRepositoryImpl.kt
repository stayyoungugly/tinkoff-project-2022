package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.mapper.ReviewEntityMapper
import com.itis.springpractice.domain.entity.ReviewEntity
import com.itis.springpractice.domain.repository.ReviewRepository

class ReviewRepositoryImpl(
    private var firestore: Firestore,
    private var reviewMapper: ReviewEntityMapper
) : ReviewRepository {

    override suspend fun addReviewOnPlace(
        placeURI: String,
        textReview: String,
        rating: Int
    ): Boolean {
        return firestore.addReviewOnPlace(placeURI, reviewMapper.mapToReview(textReview, rating))
    }

    override suspend fun getReviewsByPlace(placeURI: String): List<ReviewEntity?> {
        return reviewMapper.mapToReviewEntityList(firestore.getReviewsByPlace(placeURI))
    }
}
