package com.itis.springpractice.domain.usecase.auth

import com.itis.springpractice.domain.entity.SignUpResult
import com.itis.springpractice.domain.repository.UserAuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class RegisterUseCase (
    private val userAuthRepository: UserAuthRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): SignUpResult {
        return withContext(dispatcher) {
            userAuthRepository.register(
                email,
                password
            )
        }
    }
}
