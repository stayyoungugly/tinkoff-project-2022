package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.usecase.token.GetTokenUseCase
import kotlinx.coroutines.launch

class MainViewModel(
    private val getTokenUseCase: GetTokenUseCase
) : ViewModel() {
    private var _token: MutableLiveData<String> = MutableLiveData()
    val token: LiveData<String> = _token

    fun onGetTokenClick() {
        viewModelScope.launch {
            _token.value = getTokenUseCase()
        }
    }
}
