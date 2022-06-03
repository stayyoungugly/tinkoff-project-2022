package com.itis.springpractice.di

import android.content.SharedPreferences
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.impl.UserRepositoryImpl
import com.itis.springpractice.data.mapper.UserModelMapper
import com.itis.springpractice.domain.repository.UserRepository
import com.itis.springpractice.domain.usecase.friends.GetNumberOfUseCase
import com.itis.springpractice.domain.usecase.user.AddUserUseCase
import com.itis.springpractice.domain.usecase.user.DeleteNicknameUseCase
import com.itis.springpractice.domain.usecase.user.GetUserByNicknameUseCase
import com.itis.springpractice.domain.usecase.user.GetUserNicknameUseCase
import kotlinx.coroutines.Dispatchers

class UserContainer(
    sharedPreferences: SharedPreferences
) {
    private val userRepository: UserRepository = UserRepositoryImpl(
        firestore = Firestore(),
        userModelMapper = UserModelMapper(),
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

    val getUserNickname: GetUserNicknameUseCase = GetUserNicknameUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )

    val deleteNicknameUseCase: DeleteNicknameUseCase = DeleteNicknameUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )

    val getNumberOfUseCase: GetNumberOfUseCase = GetNumberOfUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )
}
