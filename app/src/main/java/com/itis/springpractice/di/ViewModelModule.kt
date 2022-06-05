package com.itis.springpractice.di

import com.itis.springpractice.presentation.viewmodel.*
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::EditProfileViewModel)
    viewModelOf(::FriendsViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::MapViewModel)
    viewModelOf(::PlaceInfoViewModel)
    viewModelOf(::PlaceReviewViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignUpViewModel)
    viewModelOf(::VerifyEmailViewModel)
    viewModelOf(::PlacesFavouriteViewModel)
    viewModelOf(::UserReviewsViewModel)
}
