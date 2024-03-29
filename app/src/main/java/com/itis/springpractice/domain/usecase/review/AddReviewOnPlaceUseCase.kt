package com.itis.springpractice.domain.usecase.review

import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.repository.ReviewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AddReviewOnPlaceUseCase(
    private val reviewRepository: ReviewRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        placeURI: String,
        review: Review,
    ): Boolean {
        return withContext(dispatcher) {
            reviewRepository.addReviewOnPlace(
                placeURI.drop(19),
                review
            )
        }
    }


}
