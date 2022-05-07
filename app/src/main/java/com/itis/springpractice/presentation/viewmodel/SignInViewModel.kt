package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.SignInResult
import com.itis.springpractice.domain.usecase.auth.LoginUseCase
import com.itis.springpractice.domain.usecase.network.CheckInternetUseCase
import com.itis.springpractice.domain.usecase.token.SaveTokenUseCase
import kotlinx.coroutines.launch
import java.io.IOException

class SignInViewModel(
    private val loginUseCase: LoginUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val checkInternetUseCase: CheckInternetUseCase
) : ViewModel() {
    private var _signInResult: MutableLiveData<SignInResult> = MutableLiveData()
    val signInResult: LiveData<SignInResult> = _signInResult

    fun onLoginClick(email: String, password: String) {
        viewModelScope.launch {
            _signInResult.value = loginUseCase(email, password)
        }
    }

    @Throws(InterruptedException::class, IOException::class)
    fun onCheckInternet() : Boolean {
        var flag = true
        viewModelScope.launch {
            flag = checkInternetUseCase()
        }
        return flag
    }

    fun onSaveTokenClick(idToken: String) {
        viewModelScope.launch {
            saveTokenUseCase(idToken)
        }
    }
}
