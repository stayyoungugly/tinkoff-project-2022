package com.itis.springpractice.domain.usecase.auth

import com.itis.springpractice.domain.entity.AuthErrorEntity
import com.itis.springpractice.domain.repository.UserAuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SendVerificationUseCase (
    private val userAuthRepository: UserAuthRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    suspend operator fun invoke(
        idToken: String
    ): AuthErrorEntity {
        return withContext(dispatcher) {
            userAuthRepository.sendVerification(
                idToken
            )
        }
    }
}
