package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.mapper.ReviewEntityMapper
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.repository.ReviewRepository

class ReviewRepositoryImpl(
    private var firestore: Firestore,
    private var reviewMapper: ReviewEntityMapper,
    private val preferenceManager: PreferenceManager
) : ReviewRepository {

    companion object {
        private const val DEFAULT_VALUE = ""
    }

    private val userNickname = preferenceManager.getNickname() ?: DEFAULT_VALUE

    override suspend fun addReviewOnPlace(
        placeURI: String,
        review: Review
    ): Boolean {
        return firestore.addReviewOnPlace(placeURI, reviewMapper.mapToReviewResponse(review))
    }

    override suspend fun getReviewsByPlace(placeURI: String): List<Review> {
        return reviewMapper.mapToReviewList(firestore.getReviewsByPlace(placeURI, userNickname))
            .filterNotNull()
    }

}
