package com.itis.springpractice.domain.usecase.auth

import com.itis.springpractice.domain.entity.UserInfoResult
import com.itis.springpractice.domain.repository.UserAuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetUserInfoUseCase(
    private val userAuthRepository: UserAuthRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    suspend operator fun invoke(
        idToken: String
    ): UserInfoResult {
        return withContext(dispatcher) {
            userAuthRepository.getUserInfo(
                idToken
            )
        }
    }
}
