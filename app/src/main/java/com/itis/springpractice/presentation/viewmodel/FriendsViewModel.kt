package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.usecase.friends.GetAllFriendsByNicknameUseCase
import kotlinx.coroutines.launch

class FriendsViewModel(
    private val getAllFriendsByNicknameUseCase: GetAllFriendsByNicknameUseCase
) : ViewModel() {
    private var _friends: MutableLiveData<Result<List<User>>> = MutableLiveData()
    val friends: LiveData<Result<List<User>>> = _friends

    fun onGetFriends() {
        viewModelScope.launch {
            try {
                val list = getAllFriendsByNicknameUseCase()
                _friends.value = Result.success(list)
            } catch (ex: Exception) {
                _friends.value = Result.failure(ex)
            }
        }
    }
}
