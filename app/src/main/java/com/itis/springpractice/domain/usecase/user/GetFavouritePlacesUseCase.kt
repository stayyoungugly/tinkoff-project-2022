package com.itis.springpractice.domain.usecase.user

import com.itis.springpractice.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetFavouritePlacesUseCase(
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        name: String
    ): List<String> {
        return withContext(dispatcher) {
            userRepository.getLikes(name)
        }
    }
}
