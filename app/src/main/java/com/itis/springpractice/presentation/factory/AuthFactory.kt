package com.itis.springpractice.presentation.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.viewmodel.SignInViewModel
import com.itis.springpractice.presentation.viewmodel.SignUpViewModel

class AuthFactory(
    private val authDi: UserAuthContainer,
    private val tokenDi: UserTokenContainer
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(SignInViewModel::class.java) ->
                SignInViewModel(
                    authDi.loginUseCase,
                    tokenDi.saveTokenUseCase
                ) as? T ?: throw IllegalArgumentException("Unknown ViewModel class")
            modelClass.isAssignableFrom(SignUpViewModel::class.java) ->
                SignUpViewModel(
                    authDi.registerUseCase,
                    authDi.sendVerificationUseCase,
                    tokenDi.saveTokenUseCase
                ) as? T ?: throw IllegalArgumentException("Unknown ViewModel class")
            else ->
                throw IllegalArgumentException("Unknown ViewModel class")
        }
}
