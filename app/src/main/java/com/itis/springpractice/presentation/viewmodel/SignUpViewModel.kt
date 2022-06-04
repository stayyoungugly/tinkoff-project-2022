package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.SignUpError
import com.itis.springpractice.domain.entity.SignUpSuccess
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.usecase.auth.RegisterUseCase
import com.itis.springpractice.domain.usecase.token.SaveTokenUseCase
import com.itis.springpractice.domain.usecase.user.AddUserUseCase
import com.itis.springpractice.domain.usecase.user.GetUserByNicknameUseCase
import com.itis.springpractice.presentation.ui.validation.RegistrationValidator
import kotlinx.coroutines.launch
import timber.log.Timber

class SignUpViewModel(
    private val registerUseCase: RegisterUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val addUserUseCase: AddUserUseCase,
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase,
) : ViewModel() {

    private val registrationValidator by lazy {
        RegistrationValidator()
    }

    private var _error: SingleLiveEvent<String> = SingleLiveEvent()
    val error: LiveData<String> = _error

    fun onRegisterClick(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        nickname: String
    ) {
        if (isValid(email, password, firstName, lastName, nickname)) {
            viewModelScope.launch {
                try {
                    val user: User? = getUserByNicknameUseCase(nickname)
                    if (user == null) {
                        authUser(email, password, firstName, lastName, nickname)
                    } else _error.value = "Никнейм уже существует"
                } catch (ex: Exception) {
                    Timber.e(ex)
                    _error.value = "Ошибка регистрации"
                }
            }
        }
    }

    private fun isValid(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        nickname: String
    ): Boolean {
        if (registrationValidator.isValidEmail(email) &&
            registrationValidator.isValidPassword(password) &&
            registrationValidator.isValidName(firstName) &&
            registrationValidator.isValidName(lastName) &&
            registrationValidator.isValidNickname(nickname)
        ) {
            return true
        } else when {
            !registrationValidator.isValidEmail(email) -> _error.value = "Введите корректный Email"
            !registrationValidator.isValidPassword(password) -> _error.value =
                "Пароль должен состоять из 6 символов, иметь одну букву и одну цифру"
            !registrationValidator.isValidName(firstName) -> _error.value =
                "Имя должно содержать от 2 до 15 символов и не иметь цифр"
            !registrationValidator.isValidName(lastName) -> _error.value =
                "Фамилия должна содержать от 2 до 15 символов и не иметь цифр"
            !registrationValidator.isValidNickname(nickname) -> _error.value =
                "Псевдоним должен содержать от 2 до 15 символов"
            else -> _error.value = "Ошибка в введенных данных"
        }
        return false
    }

    private fun authUser(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        nickname: String
    ) {
        viewModelScope.launch {
            try {
                registerUseCase(email, password).let {
                    when (it) {
                        is SignUpSuccess -> {
                            this@SignUpViewModel.addNewUser(
                                firstName,
                                lastName,
                                nickname,
                                it.idToken
                            )
                        }
                        is SignUpError -> {
                            when (it.reason) {
                                "EMAIL_EXISTS" -> _error.value =
                                    "Пользователь с таким Email уже существует"
                                "OPERATION_NOT_ALLOWED" -> _error.value = "Операция недоступна"
                                "TOO_MANY_ATTEMPTS_TRY_LATER" -> _error.value =
                                    "Слишком много попыток, попробуйте позже"
                                else -> _error.value = "Ошибка регистрации"
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                Timber.e(ex)
                _error.value = "Проверьте подключение к интернету"
            }
        }
    }

    private fun saveToken(idToken: String) {
        viewModelScope.launch {
            try {
                saveTokenUseCase(idToken)
                _error.value = ""
            } catch (ex: Exception) {
                Timber.e(ex)
                _error.value = "Ошибка регистрации"
            }
        }
    }

    private fun addNewUser(firstName: String, lastName: String, nickname: String, idToken: String) {
        viewModelScope.launch {
            try {
                addUserUseCase(
                    User(
                        firstName,
                        lastName,
                        nickname,
                        null
                    )
                )
                this@SignUpViewModel.saveToken(idToken)
            } catch (ex: Exception) {
                Timber.e(ex)
                _error.value = "Ошибка регистрации"
            }
        }
    }
}
