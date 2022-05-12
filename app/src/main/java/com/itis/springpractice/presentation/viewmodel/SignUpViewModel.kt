package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.SignUpResult
import com.itis.springpractice.domain.usecase.auth.RegisterUseCase
import com.itis.springpractice.domain.usecase.token.SaveTokenUseCase
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val registerUseCase: RegisterUseCase,
    private val saveTokenUseCase: SaveTokenUseCase
) : ViewModel() {
    private var _signUpResult: SingleLiveEvent<Result<SignUpResult>> = SingleLiveEvent()
    val signUpResult: LiveData<Result<SignUpResult>> = _signUpResult

    fun onRegisterClick(email: String, password: String) {
        viewModelScope.launch {
            try {
                _signUpResult.value = Result.success(registerUseCase(email, password))
            } catch (ex: Exception) {
                _signUpResult.value = Result.failure(ex)
            }
        }
    }

    fun onSaveTokenClick(idToken: String) {
        viewModelScope.launch {
            saveTokenUseCase(idToken)
        }
    }
}
