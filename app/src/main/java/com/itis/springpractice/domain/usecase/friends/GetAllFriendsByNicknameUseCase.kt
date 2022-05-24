package com.itis.springpractice.domain.usecase.friends

import com.itis.springpractice.domain.entity.UserEntity
import com.itis.springpractice.domain.repository.FriendsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAllFriendsByNicknameUseCase(
    private val friendsRepository: FriendsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    suspend operator fun invoke(): List<UserEntity> {
        return withContext(dispatcher) {
            friendsRepository.getAllFriendsByNickname()
        }
    }
}
