package com.itis.springpractice.domain.usecase.user

import com.itis.springpractice.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DeleteNicknameUseCase(
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() {
        return withContext(dispatcher) {
            userRepository.deleteNickname()
        }
    }
}
