package com.itis.springpractice.domain.usecase.friends

import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.repository.FriendsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GetAllFriendsByNicknameUseCase(
    private val friendsRepository: FriendsRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(): List<User> {
        return withContext(dispatcher) {
            friendsRepository.getAllFriendsByNickname()
        }
    }
}
