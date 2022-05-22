package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.SignInResult
import com.itis.springpractice.domain.usecase.auth.LoginUseCase
import com.itis.springpractice.domain.usecase.token.SaveTokenUseCase
import kotlinx.coroutines.launch

class SignInViewModel(
    private val loginUseCase: LoginUseCase,
    private val saveTokenUseCase: SaveTokenUseCase
) : ViewModel() {
    private var _signInResult: SingleLiveEvent<Result<SignInResult>> = SingleLiveEvent()
    val signInResult: LiveData<Result<SignInResult>> = _signInResult

    fun onLoginClick(email: String, password: String) {
        viewModelScope.launch {
            try {
                _signInResult.value = Result.success(loginUseCase(email, password))
            } catch (ex: Exception) {
                _signInResult.value = Result.failure(ex)
            }
        }
    }

    fun onSaveTokenClick(idToken: String) {
        viewModelScope.launch {
            saveTokenUseCase(idToken)
        }
    }
}
