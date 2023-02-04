package com.itis.springpractice.domain.usecase.user

import com.itis.springpractice.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class UpdateUserUseCase(
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        uploadAvatar: ByteArray
    ) {
        return withContext(dispatcher) {
            userRepository.updateUser(firstName, lastName, uploadAvatar)
        }
    }
}
