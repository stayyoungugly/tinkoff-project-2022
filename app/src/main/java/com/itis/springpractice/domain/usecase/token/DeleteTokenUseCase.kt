package com.itis.springpractice.domain.usecase.token

import com.itis.springpractice.domain.repository.UserTokenRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DeleteTokenUseCase(
    private val userTokenRepository: UserTokenRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() {
        return withContext(dispatcher) {
            userTokenRepository.deleteToken()
        }
    }
}
