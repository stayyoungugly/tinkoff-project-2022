package com.itis.springpractice.domain.usecase.review

import com.itis.springpractice.domain.repository.ReviewRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DeleteReviewUseCase(
    private val reviewRepository: ReviewRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        nickname: String,
        uri: String,
    ) {
        return withContext(dispatcher) {
            reviewRepository.deleteReview(
                nickname, uri
            )
        }
    }
}
