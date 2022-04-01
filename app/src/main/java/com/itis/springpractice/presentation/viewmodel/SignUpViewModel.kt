package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.ErrorEntity
import com.itis.springpractice.domain.entity.SignUpResult
import com.itis.springpractice.domain.usecase.auth.RegisterUseCase
import com.itis.springpractice.domain.usecase.auth.SendVerificationUseCase
import com.itis.springpractice.domain.usecase.token.SaveTokenUseCase
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val registerUseCase: RegisterUseCase,
    private val saveTokenUseCase: SaveTokenUseCase
) : ViewModel() {
    private var _signUpResult: MutableLiveData<Result<SignUpResult>> = MutableLiveData()
    val signUpResult: LiveData<Result<SignUpResult>> = _signUpResult

    fun onRegisterClick(email: String, password: String) {
        viewModelScope.launch {
            try {
                val signInResult = registerUseCase(email, password)
                _signUpResult.value = Result.success(signInResult)
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
