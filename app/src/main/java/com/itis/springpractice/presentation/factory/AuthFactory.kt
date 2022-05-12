package com.itis.springpractice.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.viewmodel.*

class AuthFactory(
    private val authDi: UserAuthContainer,
    private val tokenDi: UserTokenContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(SignInViewModel::class.java) ->
                SignInViewModel(
                    authDi.loginUseCase,
                    tokenDi.saveTokenUseCase,
                ) as? T ?: throw IllegalArgumentException("Unknown ViewModel class")
            modelClass.isAssignableFrom(SignUpViewModel::class.java) ->
                SignUpViewModel(
                    authDi.registerUseCase,
                    tokenDi.saveTokenUseCase,
                ) as? T ?: throw IllegalArgumentException("Unknown ViewModel class")
            modelClass.isAssignableFrom(VerifyEmailViewModel::class.java) ->
                VerifyEmailViewModel(
                    authDi.getUserInfoUseCase,
                    tokenDi.getTokenUseCase,
                    authDi.sendVerificationUseCase
                ) as? T ?: throw IllegalArgumentException("Unknown ViewModel class")
            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                MainViewModel(
                    tokenDi.getTokenUseCase
                ) as? T ?: throw IllegalArgumentException("Unknown ViewModel class")
            modelClass.isAssignableFrom(MapViewModel::class.java) ->
                MapViewModel(
                    tokenDi.deleteTokenUseCase,
                    authDi.checkLocationPermissionUseCase
                ) as? T ?: throw IllegalArgumentException("Unknown ViewModel class")
            else ->
                throw IllegalArgumentException("Unknown ViewModel class")
        }
}
