package com.itis.springpractice.domain.usecase.user

import com.itis.springpractice.domain.entity.UserEntity
import com.itis.springpractice.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetUserByNicknameUseCase(
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    suspend operator fun invoke(
        nickname: String
    ): UserEntity? {
        return withContext(dispatcher) {
            userRepository.getUserByNickname(nickname)
        }
    }
}