package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.SignUpResult
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.usecase.auth.RegisterUseCase
import com.itis.springpractice.domain.usecase.token.SaveTokenUseCase
import com.itis.springpractice.domain.usecase.user.AddUserUseCase
import com.itis.springpractice.domain.usecase.user.GetUserByNicknameUseCase
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val registerUseCase: RegisterUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase
) : ViewModel() {

    private var _nicknameResult: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val nicknameResult: LiveData<Boolean> = _nicknameResult

    fun isNicknameAvailable(nickname: String): Boolean {
        var check = true
        viewModelScope.launch {
            val user: User? = getUserByNicknameUseCase(nickname)
            user?.let {
                check = false
            }
        }
        return check
    }

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

    fun addNewUser(firstName: String, lastName: String, nickname: String) {
        viewModelScope.launch {
            addUserUseCase(User(firstName, lastName, nickname))
        }
    }
}
