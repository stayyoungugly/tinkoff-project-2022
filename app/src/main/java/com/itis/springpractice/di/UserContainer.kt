package com.itis.springpractice.di

import android.content.SharedPreferences
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.impl.ReviewRepositoryImpl
import com.itis.springpractice.data.impl.UserRepositoryImpl
import com.itis.springpractice.data.mapper.ReviewEntityMapper
import com.itis.springpractice.data.mapper.UserModelMapper
import com.itis.springpractice.domain.repository.ReviewRepository
import com.itis.springpractice.domain.repository.UserRepository
import com.itis.springpractice.domain.usecase.user.GetNumberOfUseCase
import com.itis.springpractice.domain.usecase.review.AddReviewOnPlaceUseCase
import com.itis.springpractice.domain.usecase.review.DeleteReviewUseCase
import com.itis.springpractice.domain.usecase.review.GetReviewsByPlaceUseCase
import com.itis.springpractice.domain.usecase.user.*
import kotlinx.coroutines.Dispatchers

class UserContainer(
    sharedPreferences: SharedPreferences
) {
    private val userRepository: UserRepository = UserRepositoryImpl(
        firestore = Firestore(),
        userModelMapper = UserModelMapper(),
        preferenceManager = PreferenceManager(sharedPreferences)
    )

    val getFavouritePlacesUseCase: GetFavouritePlacesUseCase = GetFavouritePlacesUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
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
        reviewMapper = ReviewEntityMapper(getUserByNicknameUseCase),
        preferenceManager = PreferenceManager(sharedPreferences)
    )

    val getReviewsByPlaceUseCase: GetReviewsByPlaceUseCase = GetReviewsByPlaceUseCase(
        reviewRepository = reviewRepository,
        dispatcher = Dispatchers.Default
    )

    val deleteReviewUseCase: DeleteReviewUseCase = DeleteReviewUseCase(
        reviewRepository = reviewRepository,
        dispatcher = Dispatchers.Default
    )

    val getUserReviewsUseCase: GetUserReviewsUseCase = GetUserReviewsUseCase(
        reviewRepository = reviewRepository,
        dispatcher = Dispatchers.Default
    )

    val addReviewOnPlaceUseCase: AddReviewOnPlaceUseCase = AddReviewOnPlaceUseCase(
        reviewRepository = reviewRepository,
        dispatcher = Dispatchers.Default,
    )

    val getUserNicknameUseCase: GetUserNicknameUseCase = GetUserNicknameUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )

    val deleteNicknameUseCase: DeleteNicknameUseCase = DeleteNicknameUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )
    val addUserLikeUseCase: AddUserLikeUseCase = AddUserLikeUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )

    val deleteUserLikeUseCase: DeleteUserLikeUseCase = DeleteUserLikeUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )
    val isPlaceLikedUseCase: IsPlaceLikedUseCase = IsPlaceLikedUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )

    val getNumberOfUseCase: GetNumberOfUseCase = GetNumberOfUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )

    val updateUserUseCase: UpdateUserUseCase = UpdateUserUseCase(
        userRepository = userRepository,
        dispatcher = Dispatchers.Default
    )
}
