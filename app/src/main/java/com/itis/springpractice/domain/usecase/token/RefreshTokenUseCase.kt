package com.itis.springpractice.domain.usecase.token

import com.itis.springpractice.domain.entity.TokenResult
import com.itis.springpractice.domain.repository.UserTokenRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RefreshTokenUseCase(
    private val userTokenRepository: UserTokenRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): TokenResult {
        return withContext(dispatcher) {
            userTokenRepository.refreshToken()
        }
    }
}
