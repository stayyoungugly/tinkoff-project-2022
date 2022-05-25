package com.itis.springpractice.domain.usecase.review

import com.itis.springpractice.domain.repository.ReviewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddReviewOnPlaceUseCase(
    private val reviewRepository: ReviewRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    suspend operator fun invoke(
        placeURI: String,
        textReview: String,
        rating: Int
    ): Boolean {
        return withContext(dispatcher) {
            reviewRepository.addReviewOnPlace(placeURI.takeLast(10), textReview, rating)
        }
    }
}
