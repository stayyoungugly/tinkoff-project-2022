package com.itis.springpractice.di

import android.content.SharedPreferences
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.impl.UserRepositoryImpl
import com.itis.springpractice.data.mapper.UserEntityMapper
import com.itis.springpractice.domain.repository.UserRepository
import com.itis.springpractice.domain.usecase.user.AddUserUseCase
import com.itis.springpractice.domain.usecase.user.GetUserByNicknameUseCase
import kotlinx.coroutines.Dispatchers

class UserContainer(
    sharedPreferences: SharedPreferences
) {
    private val userRepository: UserRepository = UserRepositoryImpl(
        firestore = Firestore(),
        userEntityMapper = UserEntityMapper(),
        preferenceManager = PreferenceManager(sharedPreferences)
    )

    val addUserUseCase: AddUserUseCase = AddUserUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )

    val getUserByNicknameUseCase: GetUserByNicknameUseCase = GetUserByNicknameUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )
}
