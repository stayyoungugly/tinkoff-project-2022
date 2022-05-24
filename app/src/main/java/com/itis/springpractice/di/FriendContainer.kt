package com.itis.springpractice.di

import android.content.SharedPreferences
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.impl.FriendsRepositoryImpl
import com.itis.springpractice.data.mapper.UserEntityMapper
import com.itis.springpractice.domain.repository.FriendsRepository
import com.itis.springpractice.domain.usecase.friends.AddFriendUseCase
import com.itis.springpractice.domain.usecase.friends.GetAllFriendsByNicknameUseCase
import kotlinx.coroutines.Dispatchers

class FriendContainer(
    sharedPreferences: SharedPreferences
) {
    private val friendsRepository: FriendsRepository = FriendsRepositoryImpl(
        firestore = Firestore(),
        userEntityMapper = UserEntityMapper(),
        preferenceManager = PreferenceManager(sharedPreferences)
    )

    val addFriendUseCase: AddFriendUseCase = AddFriendUseCase(
        friendsRepository = friendsRepository,
        dispatcher = Dispatchers.Default
    )

    val getAllFriendsByNicknameUseCase: GetAllFriendsByNicknameUseCase =
        GetAllFriendsByNicknameUseCase(
            friendsRepository = friendsRepository,
            dispatcher = Dispatchers.Default
        )
}
