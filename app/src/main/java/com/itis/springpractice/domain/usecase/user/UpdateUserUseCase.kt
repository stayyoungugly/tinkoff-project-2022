package com.itis.springpractice.domain.usecase.user

import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateUserUseCase(
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        user: User
    ) {
        return withContext(dispatcher) {
            userRepository.updateUser(user)
        }
    }
}
