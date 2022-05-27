package com.itis.springpractice.data.mapper

import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.response.ReviewResponse
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.repository.UserRepository

class ReviewEntityMapper(
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) {
    private suspend fun mapToReview(review: ReviewResponse): Review? {
        return if (!review.authorNickname.isNullOrEmpty() && !review.textReview.isNullOrEmpty() && (review.rating != null)) {
            userRepository.getUserByNickname(review.authorNickname!!)?.let {
                Review(
                    author = it,
                    textReview = review.textReview!!,
                    rating = review.rating!!
                )
            }
        } else null
    }

    suspend fun mapToReviewList(list: List<ReviewResponse>): List<Review?> =
        list.map { review -> mapToReview(review) }

    fun mapToReviewResponse(textReviewValue: String, ratingValue: Int) =
        ReviewResponse(
            authorNickname = preferenceManager.getNickname(),
            textReview = textReviewValue,
            rating = ratingValue
        )
}
