package com.itis.springpractice.di

import com.itis.springpractice.domain.usecase.auth.*
import com.itis.springpractice.domain.usecase.friends.AddFriendUseCase
import com.itis.springpractice.domain.usecase.friends.DeleteFriendUseCase
import com.itis.springpractice.domain.usecase.friends.GetAllFriendsByNicknameUseCase
import com.itis.springpractice.domain.usecase.friends.IsUserFriendUseCase
import com.itis.springpractice.domain.usecase.review.AddReviewOnPlaceUseCase
import com.itis.springpractice.domain.usecase.review.DeleteReviewUseCase
import com.itis.springpractice.domain.usecase.review.GetReviewsByPlaceUseCase
import com.itis.springpractice.domain.usecase.token.*
import com.itis.springpractice.domain.usecase.user.*
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    //auth
    factoryOf(::DeleteUserUseCase)
    factoryOf(::GetUserInfoUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    factoryOf(::SendVerificationUseCase)

    //friends
    factoryOf(::AddFriendUseCase)
    factoryOf(::DeleteFriendUseCase)
    factoryOf(::GetAllFriendsByNicknameUseCase)
    factoryOf(::IsUserFriendUseCase)

    //review
    factoryOf(::AddReviewOnPlaceUseCase)
    factoryOf(::DeleteReviewUseCase)
    factoryOf(::GetReviewsByPlaceUseCase)

    //token
    factoryOf(::DeleteTokenUseCase)
    factoryOf(::GetRefreshTokenUseCase)
    factoryOf(::GetTokenUseCase)
    factoryOf(::RefreshTokenUseCase)
    factoryOf(::SaveRefreshTokenUseCase)
    factoryOf(::SaveTokenUseCase)

    //user
    factoryOf(::AddUserLikeUseCase)
    factoryOf(::AddUserUseCase)
    factoryOf(::DeleteNicknameUseCase)
    factoryOf(::DeleteUserLikeUseCase)
    factoryOf(::GetFavouritePlacesUseCase)
    factoryOf(::GetNumberOfUseCase)
    factoryOf(::GetUserByNicknameUseCase)
    factoryOf(::GetUserNicknameUseCase)
    factoryOf(::GetUserReviewsUseCase)
    factoryOf(::IsPlaceLikedUseCase)
    factoryOf(::UpdateUserUseCase)
    factoryOf(::UpdateNicknameUseCase)

    factory { Dispatchers.Default }
}
