package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.ErrorModel
import com.itis.springpractice.domain.entity.UserInfoResult
import com.itis.springpractice.domain.usecase.auth.GetUserInfoUseCase
import com.itis.springpractice.domain.usecase.auth.SendVerificationUseCase
import com.itis.springpractice.domain.usecase.token.GetTokenUseCase
import kotlinx.coroutines.launch

class VerifyEmailViewModel(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val sendVerificationUseCase: SendVerificationUseCase
) : ViewModel() {

    private var _userInfoResult: SingleLiveEvent<Result<UserInfoResult>> = SingleLiveEvent()
    val userInfoResult: LiveData<Result<UserInfoResult>> = _userInfoResult

    fun onGetUserInfoClick() {
        viewModelScope.launch {
            val idToken = getToken()
            try {
                val userInfoResult = getUserInfoUseCase(idToken)
                _userInfoResult.value = Result.success(userInfoResult)
            } catch (ex: Exception) {
                _userInfoResult.value = Result.failure(ex)
            }
        }
    }

    private var token: String? = null
    private suspend fun getToken(): String {
        return token ?: getTokenUseCase().also { token = it }
    }

    private var _errorModel: MutableLiveData<Result<ErrorModel>> = MutableLiveData()
    val errorModel: LiveData<Result<ErrorModel>> = _errorModel

    fun onSendVerificationClick() {
        viewModelScope.launch {
            val idToken = getToken()
            try {
                val errorEntity = sendVerificationUseCase(idToken)
                _errorModel.value = Result.success(errorEntity)
            } catch (ex: Exception) {
                _errorModel.value = Result.failure(ex)
            }
        }
    }
}
