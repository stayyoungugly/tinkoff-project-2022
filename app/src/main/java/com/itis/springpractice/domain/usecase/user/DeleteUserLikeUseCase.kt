package com.itis.springpractice.domain.usecase.user

import com.itis.springpractice.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DeleteUserLikeUseCase(
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(nickname: String, uri: String) {
        return withContext(dispatcher) {
            userRepository.deleteUserLike(nickname, uri.drop(19))
        }
    }
}
