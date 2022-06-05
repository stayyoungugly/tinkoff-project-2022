package com.itis.springpractice.domain.usecase.user

import com.itis.springpractice.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetNumberOfUseCase(
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        nickname: String
    ): HashMap<String, Int> {
        return withContext(dispatcher) {
            userRepository.getNumberOf(nickname)
        }
    }
}
