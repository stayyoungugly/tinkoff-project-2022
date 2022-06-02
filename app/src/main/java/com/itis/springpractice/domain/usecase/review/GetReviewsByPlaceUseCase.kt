package com.itis.springpractice.domain.usecase.review

import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.repository.ReviewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetReviewsByPlaceUseCase(
    private val reviewRepository: ReviewRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    suspend operator fun invoke(
        placeURI: String
    ): List<Review> {
        return withContext(dispatcher) {
            reviewRepository.getReviewsByPlace(placeURI.takeLast(10))
        }
    }
}
