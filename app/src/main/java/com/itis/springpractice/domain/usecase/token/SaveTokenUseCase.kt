package com.itis.springpractice.domain.usecase.token

import com.itis.springpractice.domain.repository.UserTokenRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SaveTokenUseCase(
    private val userTokenRepository: UserTokenRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        idToken: String
    ) {
        return withContext(dispatcher) {
            userTokenRepository.saveToken(idToken)
        }
    }
}
