package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.mapper.ReviewEntityMapper
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.repository.ReviewRepository

class ReviewRepositoryImpl(
    private var firestore: Firestore,
    private var reviewMapper: ReviewEntityMapper,
) : ReviewRepository {

    override suspend fun addReviewOnPlace(
        placeURI: String,
        review: Review
    ): Boolean {
        return firestore.addReviewOnPlace(placeURI, reviewMapper.mapToReviewResponse(review))
    }

    override suspend fun getReviewsByPlace(placeURI: String): List<Review> {
        return reviewMapper.mapToReviewList(firestore.getReviewsByPlace(placeURI)).filterNotNull()
    }

}
