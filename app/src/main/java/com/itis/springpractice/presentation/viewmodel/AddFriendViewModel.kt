package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.usecase.friends.AddFriendUseCase
import com.itis.springpractice.domain.usecase.user.GetUserByNicknameUseCase
import kotlinx.coroutines.launch

class AddFriendViewModel(
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase,
    private val addFriendUseCase: AddFriendUseCase
) : ViewModel() {
    private var _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    fun onAddFriend(nickname: String) {
        viewModelScope.launch {
            try {
                val user: User? = getUserByNicknameUseCase(nickname)
                if (user == null) {
                    _message.value = "Пользователя с таким псевдонимом не существует"
                }
                else {
                    addFriendUseCase(nickname)
                    _message.value = "Добавлен новый друг"
                }
            } catch (ex: Exception) {
                _message.value = "Произошла ошибка"
            }
        }
    }
}
