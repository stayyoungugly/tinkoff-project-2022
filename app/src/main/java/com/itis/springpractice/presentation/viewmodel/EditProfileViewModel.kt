package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.usecase.user.GetUserByNicknameUseCase
import com.itis.springpractice.domain.usecase.user.GetUserNicknameUseCase
import com.itis.springpractice.domain.usecase.user.UpdateUserUseCase
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase,
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val updateUserUseCase: UpdateUserUseCase
) : ViewModel() {
    fun onUpdateUser(firstName: String, lastName: String, nickname: String) {
        viewModelScope.launch {
            val user = User(firstName, lastName, nickname)
            updateUserUseCase(user)
        }
    }

    private var _user: SingleLiveEvent<Result<User>> = SingleLiveEvent()
    val user: LiveData<Result<User>> = _user

    fun onGetUserInfo() {
        viewModelScope.launch {
            try {
                getUserByNicknameUseCase(getUserNicknameUseCase())?.let {
                    _user.value = Result.success(it)
                }
            } catch (ex: Exception) {
                _user.value = Result.failure(ex)
            }
        }
    }
}
