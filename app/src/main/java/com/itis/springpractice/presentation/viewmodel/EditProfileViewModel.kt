package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.usecase.user.GetUserByNicknameUseCase
import com.itis.springpractice.domain.usecase.user.GetUserNicknameUseCase
import com.itis.springpractice.domain.usecase.user.UpdateUserUseCase
import com.itis.springpractice.presentation.ui.validation.RegistrationValidator
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase,
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
) : ViewModel() {

    private val registrationValidator by lazy {
        RegistrationValidator()
    }

    private var _error: MutableLiveData<String> = MutableLiveData()
    val error: LiveData<String> = _error

    fun onUpdateUser(firstName: String, lastName: String, data: ByteArray) {
        if (isValid(firstName, lastName)) {
            viewModelScope.launch {
                try {
                    updateUserUseCase(
                        firstName,
                        lastName,
                        data
                    )
                    _error.value = ""
                } catch (ex: Exception) {
                    _error.value = "Ошибка редактирования"
                }
            }
        }
    }

    private fun isValid(
        firstName: String,
        lastName: String,
    ): Boolean {
        if (registrationValidator.isValidName(firstName) &&
            registrationValidator.isValidName(lastName)
        ) {
            return true
        } else when {
            !registrationValidator.isValidName(firstName) -> _error.value =
                "Имя должно содержать от 2 до 15 символов и не иметь цифр"
            !registrationValidator.isValidName(lastName) -> _error.value =
                "Фамилия должна содержать от 2 до 15 символов и не иметь цифр"
            else -> _error.value = "Ошибка в введенных данных"
        }
        return false
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
