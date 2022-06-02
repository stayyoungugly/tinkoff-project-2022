package com.itis.springpractice.domain.usecase.review

import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.repository.ReviewRepository
import com.itis.springpractice.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddReviewOnPlaceUseCase(
    private val reviewRepository: ReviewRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) {
    suspend operator fun invoke(
        placeURI: String,
        review: Review,
    ): Boolean {
        return withContext(dispatcher) {
            reviewRepository.addReviewOnPlace(
                placeURI.takeLast(10),
                review
            )
        }
    }


}
