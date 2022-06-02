package com.itis.springpractice.di

import android.content.SharedPreferences
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.impl.ReviewRepositoryImpl
import com.itis.springpractice.data.impl.UserRepositoryImpl
import com.itis.springpractice.data.mapper.ReviewEntityMapper
import com.itis.springpractice.data.mapper.UserEntityMapper
import com.itis.springpractice.domain.repository.ReviewRepository
import com.itis.springpractice.domain.repository.UserRepository
import com.itis.springpractice.domain.usecase.review.AddReviewOnPlaceUseCase
import com.itis.springpractice.domain.usecase.review.GetReviewsByPlaceUseCase
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

    private val reviewRepository: ReviewRepository = ReviewRepositoryImpl(
        firestore = Firestore(),
        reviewMapper = ReviewEntityMapper(userRepository, PreferenceManager(sharedPreferences))
    )

    val getReviewsByPlaceUseCase: GetReviewsByPlaceUseCase = GetReviewsByPlaceUseCase(
        reviewRepository = reviewRepository,
        dispatcher = Dispatchers.Default
    )

    val addReviewOnPlaceUseCase: AddReviewOnPlaceUseCase = AddReviewOnPlaceUseCase(
        reviewRepository = reviewRepository,
        dispatcher = Dispatchers.Default,
        userRepository = userRepository,
        preferenceManager = PreferenceManager(sharedPreferences)
    )
    val getUserNickname: GetUserNicknameUseCase = GetUserNicknameUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )

    val deleteNicknameUseCase: DeleteNicknameUseCase = DeleteNicknameUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )
}
