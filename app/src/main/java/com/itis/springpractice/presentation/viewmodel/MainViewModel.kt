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
    private var _token: MutableLiveData<Result<String>> = MutableLiveData()
    val token: LiveData<Result<String>> = _token

    fun onGetTokenClick() {
        viewModelScope.launch {
            try {
                val token = getTokenUseCase()
                _token.value = Result.success(token)
            } catch (ex: Exception) {
                _token.value = Result.failure(ex)
            }
        }
    }
}
