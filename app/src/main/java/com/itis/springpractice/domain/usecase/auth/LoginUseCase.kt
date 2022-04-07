package com.itis.springpractice.domain.usecase.auth

import com.itis.springpractice.domain.entity.SignInResult
import com.itis.springpractice.domain.repository.UserAuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginUseCase (
    private val userAuthRepository: UserAuthRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): SignInResult {
        return withContext(dispatcher) {
            userAuthRepository.login(
                email,
                password
            )
        }
    }
}
