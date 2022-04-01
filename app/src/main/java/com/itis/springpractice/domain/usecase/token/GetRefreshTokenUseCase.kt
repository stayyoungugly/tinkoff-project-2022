package com.itis.springpractice.domain.usecase.token

import com.itis.springpractice.domain.repository.UserTokenRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetRefreshTokenUseCase(
    private val userTokenRepository: UserTokenRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    suspend operator fun invoke(): String {
        return withContext(dispatcher) {
            userTokenRepository.getRefreshToken()
        }
    }
}
