package com.itis.springpractice.domain.usecase.user

import com.itis.springpractice.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class IsPlaceLikedUseCase(
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(nickname: String, uri: String): String? {
        return withContext(dispatcher) {
            userRepository.isPlaceLiked(nickname, uri.drop(19))
        }
    }
}
