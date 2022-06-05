package com.itis.springpractice.data.mapper

import com.itis.springpractice.data.response.ReviewResponse
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.usecase.user.GetUserByNicknameUseCase

class ReviewEntityMapper(
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase
) {
    private suspend fun mapToReview(review: ReviewResponse): Review? {
        return if (!review.authorNickname.isNullOrEmpty() && !review.textReview.isNullOrEmpty() && (review.rating != null)) {
            getUserByNicknameUseCase(review.authorNickname!!)?.let {
                Review(
                    author = it,
                    textReview = review.textReview!!,
                    rating = review.rating!!,
                    created = review.created!!,
                    uri = review.uri!!
                )
            }
        } else null
    }

    suspend fun mapToReviewList(list: List<ReviewResponse>): List<Review?> =
        list.map { review -> mapToReview(review) }

    fun mapToReviewResponse(review: Review) =
        ReviewResponse(
            authorNickname = review.author.nickname,
            textReview = review.textReview,
            rating = review.rating,
            created = review.created,
            uri = review.uri
        )
}
