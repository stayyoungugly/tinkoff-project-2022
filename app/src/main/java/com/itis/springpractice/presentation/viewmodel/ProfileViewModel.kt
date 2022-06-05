package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.usecase.friends.GetNumberOfUseCase
import com.itis.springpractice.domain.usecase.token.DeleteTokenUseCase
import com.itis.springpractice.domain.usecase.user.DeleteNicknameUseCase
import com.itis.springpractice.domain.usecase.user.GetUserByNicknameUseCase
import com.itis.springpractice.domain.usecase.user.GetUserNicknameUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase,
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val getNumberOfUseCase: GetNumberOfUseCase,
    private val deleteTokenUseCase: DeleteTokenUseCase,
    private val deleteNicknameUseCase: DeleteNicknameUseCase
) : ViewModel() {
    private var _user: SingleLiveEvent<Result<User>> = SingleLiveEvent()
    val user: LiveData<Result<User>> = _user

    private var _isUser: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isUser: LiveData<Boolean> = _isUser

    fun onGetUserInfo(nickname: String?) {
        viewModelScope.launch {
            try {
                val user = if (nickname != null) {
                    _isUser.value = false
                    getUserByNicknameUseCase(nickname)
                } else {
                    _isUser.value = true
                    getUserByNicknameUseCase(getUserNicknameUseCase())
                }
                user?.let {
                    _user.value = Result.success(it)
                }
            } catch (ex: Exception) {
                _user.value = Result.failure(ex)
            }
        }
    }

    private var _number: SingleLiveEvent<HashMap<String, Int>> = SingleLiveEvent()
    val number: LiveData<HashMap<String, Int>> = _number

    fun onGetNumberOf(nickname: String?) {
        viewModelScope.launch {
            _number.value = if (nickname != null) {
                getNumberOfUseCase(nickname)
            } else {
                getNumberOfUseCase(getUserNicknameUseCase())
            }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            deleteTokenUseCase()
            deleteNicknameUseCase()
        }
    }
}
