package com.itis.springpractice.domain.usecase.user

import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.repository.ReviewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetUserReviewsUseCase(
    private val reviewRepository: ReviewRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        name: String
    ): List<Review> {
        return withContext(dispatcher) {
            reviewRepository.getUserReviews(name)
        }
    }
}
