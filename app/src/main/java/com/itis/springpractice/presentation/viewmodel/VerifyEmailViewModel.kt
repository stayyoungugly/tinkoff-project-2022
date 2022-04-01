package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.SignInResult
import com.itis.springpractice.domain.usecase.auth.GetUserInfoUseCase
import com.itis.springpractice.domain.usecase.auth.LoginUseCase
import com.itis.springpractice.domain.usecase.token.SaveTokenUseCase
import kotlinx.coroutines.launch

class VerifyEmailViewModel(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val saveTokenUseCase: SaveTokenUseCase
) : ViewModel() {

    fun onSaveTokenClick(idToken: String) {
        viewModelScope.launch {
            saveTokenUseCase(idToken)
        }
    }
}
