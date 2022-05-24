package com.itis.springpractice.domain.usecase.friends

import com.itis.springpractice.domain.repository.FriendsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AddFriendUseCase(
    private val friendsRepository: FriendsRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    suspend operator fun invoke(
        nickname: String
    ) {
        return withContext(dispatcher) {
            friendsRepository.addFriend(nickname)
        }
    }
}
