package com.itis.springpractice.data.mapper

import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.request.Review
import com.itis.springpractice.domain.entity.ReviewEntity
import com.itis.springpractice.domain.repository.UserRepository

class ReviewEntityMapper(
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) {
    private suspend fun mapToReviewEntity(review: Review): ReviewEntity? {
        return if (!review.authorNickname.isNullOrEmpty() && !review.textReview.isNullOrEmpty() && (review.rating != null)) {
            userRepository.getUserByNickname(review.authorNickname!!)?.let {
                ReviewEntity(
                    author = it,
                    textReview = review.textReview!!,
                    rating = review.rating!!
                )
            }
        } else null
    }

    suspend fun mapToReviewEntityList(list: List<Review>): List<ReviewEntity?> =
        list.map { review -> mapToReviewEntity(review) }

    fun mapToReview(textReviewValue: String, ratingValue: Int) =
        Review(
            authorNickname = preferenceManager.getNickname(),
            textReview = textReviewValue,
            rating = ratingValue
        )
}
