package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.usecase.token.DeleteTokenUseCase
import com.itis.springpractice.domain.usecase.token.GetTokenUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val deleteTokenUseCase: DeleteTokenUseCase
) : ViewModel() {

    fun onDeleteTokenClick() {
        viewModelScope.launch {
            deleteTokenUseCase()
        }
    }
}
