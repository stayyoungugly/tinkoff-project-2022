package com.itis.springpractice.di

import android.content.SharedPreferences
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.impl.FriendsRepositoryImpl
import com.itis.springpractice.data.mapper.UserModelMapper
import com.itis.springpractice.domain.usecase.friends.AddFriendUseCase
import com.itis.springpractice.domain.usecase.friends.DeleteFriendUseCase
import com.itis.springpractice.domain.usecase.friends.GetAllFriendsByNicknameUseCase
import com.itis.springpractice.domain.usecase.friends.IsUserFriendUseCase
import kotlinx.coroutines.Dispatchers

class FriendContainer(
    sharedPreferences: SharedPreferences
) {
    private val friendsRepository = FriendsRepositoryImpl(
        Firestore(),
        UserModelMapper(),
        PreferenceManager(sharedPreferences)
    )

    val addFriendUseCase = AddFriendUseCase(
        friendsRepository,
        Dispatchers.Default
    )

    val getAllFriendsByNicknameUseCase = GetAllFriendsByNicknameUseCase(
        friendsRepository,
        Dispatchers.Default
    )
    val isUserFriendUseCase = IsUserFriendUseCase(
        friendsRepository,
        Dispatchers.Default
    )

    val deleteFriendUseCase = DeleteFriendUseCase(
        friendsRepository,
        Dispatchers.Default
    )
}
