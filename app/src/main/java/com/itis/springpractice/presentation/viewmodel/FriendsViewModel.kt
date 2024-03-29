package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.usecase.friends.AddFriendUseCase
import com.itis.springpractice.domain.usecase.friends.DeleteFriendUseCase
import com.itis.springpractice.domain.usecase.friends.GetAllFriendsByNicknameUseCase
import com.itis.springpractice.domain.usecase.friends.IsUserFriendUseCase
import com.itis.springpractice.domain.usecase.user.GetUserByNicknameUseCase
import com.itis.springpractice.domain.usecase.user.GetUserNicknameUseCase
import kotlinx.coroutines.launch

class FriendsViewModel(
    private val getAllFriendsByNicknameUseCase: GetAllFriendsByNicknameUseCase,
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase,
    private val addFriendUseCase: AddFriendUseCase,
    private val isUserFriendUseCase: IsUserFriendUseCase,
    private val deleteFriendUseCase: DeleteFriendUseCase
) : ViewModel() {
    private var _friends: SingleLiveEvent<Result<List<User>>> = SingleLiveEvent()
    val friends: LiveData<Result<List<User>>> = _friends

    private var _isUser: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isUser: LiveData<Boolean> = _isUser

    fun onGetFriends(nickname: String?) {
        viewModelScope.launch {
            try {
                val list = if (nickname != null && nickname != getUserNicknameUseCase()) {
                    _isUser.value = false
                    getAllFriendsByNicknameUseCase(nickname)
                } else {
                    _isUser.value = true
                    getAllFriendsByNicknameUseCase(getUserNicknameUseCase())
                }
                _friends.value = Result.success(list)
            } catch (ex: Exception) {
                _friends.value = Result.failure(ex)
            }
        }
    }

    private var _message: MutableLiveData<String> = MutableLiveData()
    val message: LiveData<String> = _message

    fun onAddFriend(nickname: String) {
        viewModelScope.launch {
            try {
                val user: User? = getUserByNicknameUseCase(nickname)
                when {
                    nickname == getUserNicknameUseCase() -> _message.value =
                        "Нельзя дружить с самим собой"
                    isUserFriendUseCase(nickname) -> _message.value = "Этот пользователь уже друг"
                    user == null -> _message.value =
                        "Пользователя с таким псевдонимом не существует"
                    else -> {
                        addFriendUseCase(nickname)
                        val list = getAllFriendsByNicknameUseCase(getUserNicknameUseCase())
                        _friends.value = Result.success(list)
                        _message.value = "OK"
                    }
                }
            } catch (ex: Exception) {
                _message.value = "Произошла ошибка добавления"
            }
        }
    }

    fun onDeleteFriend(nickname: String) {
        viewModelScope.launch {
            try {
                deleteFriendUseCase(nickname)
                val list = getAllFriendsByNicknameUseCase(getUserNicknameUseCase())
                _friends.value = Result.success(list)
            } catch (ex: Exception) {
                _message.value = "Произошла ошибка удаления"
            }
        }
    }
}
