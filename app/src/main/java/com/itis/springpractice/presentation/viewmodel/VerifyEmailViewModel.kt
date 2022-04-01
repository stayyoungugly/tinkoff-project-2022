package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.ErrorEntity
import com.itis.springpractice.domain.entity.SignInResult
import com.itis.springpractice.domain.entity.UserInfoError
import com.itis.springpractice.domain.entity.UserInfoResult
import com.itis.springpractice.domain.usecase.auth.GetUserInfoUseCase
import com.itis.springpractice.domain.usecase.auth.LoginUseCase
import com.itis.springpractice.domain.usecase.auth.SendVerificationUseCase
import com.itis.springpractice.domain.usecase.token.GetTokenUseCase
import com.itis.springpractice.domain.usecase.token.SaveTokenUseCase
import kotlinx.coroutines.launch

class VerifyEmailViewModel(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val sendVerificationUseCase: SendVerificationUseCase
) : ViewModel() {

    private var _userInfoResult: MutableLiveData<Result<UserInfoResult>> = MutableLiveData()
    val userInfoResult: LiveData<Result<UserInfoResult>> = _userInfoResult

    fun onGetUserInfoClick(
        idToken: String
    ) {
        viewModelScope.launch {
            try {
                val userInfoResult = getUserInfoUseCase(idToken)
                _userInfoResult.value = Result.success(userInfoResult)
            } catch (ex: Exception) {
                _userInfoResult.value = Result.failure(ex)
            }
        }
    }

    private var _token: MutableLiveData<Result<String>> = MutableLiveData()
    val token: LiveData<Result<String>> = _token

    fun onGetTokenClick() {
        viewModelScope.launch {
            try {
                val token = getTokenUseCase()
                _token.value = Result.success(token)
            } catch (ex: Exception) {
                _token.value = Result.failure(ex)
            }
        }
    }

    private var _errorEntity: MutableLiveData<Result<ErrorEntity>> = MutableLiveData()
    val errorEntity: LiveData<Result<ErrorEntity>> = _errorEntity

    fun onSendVerificationClick(idToken: String) {
        viewModelScope.launch {
            try {
                val errorEntity = sendVerificationUseCase(idToken)
                _errorEntity.value = Result.success(errorEntity)
            } catch (ex: Exception) {
                _errorEntity.value = Result.failure(ex)
            }
        }
    }
}
