package com.itis.springpractice.domain.usecase.auth

import com.itis.springpractice.domain.entity.ErrorModel
import com.itis.springpractice.domain.repository.UserAuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DeleteUserUseCase(
    private val userAuthRepository: UserAuthRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        idToken: String
    ): ErrorModel {
        return withContext(dispatcher) {
            userAuthRepository.deleteUser(
                idToken
            )
        }
    }
}
