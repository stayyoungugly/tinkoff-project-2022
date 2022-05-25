package com.itis.springpractice.domain.usecase.auth

import com.itis.springpractice.domain.entity.ErrorEntity
import com.itis.springpractice.domain.repository.UserAuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SendVerificationUseCase (
    private val userAuthRepository: UserAuthRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        idToken: String
    ): ErrorEntity {
        return withContext(dispatcher) {
            userAuthRepository.sendVerification(
                idToken
            )
        }
    }
}
