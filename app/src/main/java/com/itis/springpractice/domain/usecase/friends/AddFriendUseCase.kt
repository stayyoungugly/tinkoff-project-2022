package com.itis.springpractice.domain.usecase.friends

import com.itis.springpractice.domain.repository.FriendsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AddFriendUseCase(
    private val friendsRepository: FriendsRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        nickname: String
    ) {
        return withContext(dispatcher) {
            friendsRepository.addFriend(nickname)
        }
    }
}
