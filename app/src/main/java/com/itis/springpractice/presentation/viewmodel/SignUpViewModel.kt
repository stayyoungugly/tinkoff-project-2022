package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.SignUpResult
import com.itis.springpractice.domain.usecase.auth.RegisterUseCase
import com.itis.springpractice.domain.usecase.network.CheckInternetUseCase
import com.itis.springpractice.domain.usecase.token.SaveTokenUseCase
import kotlinx.coroutines.launch
import java.io.IOException

class SignUpViewModel(
    private val registerUseCase: RegisterUseCase,
    private val saveTokenUseCase: SaveTokenUseCase,
    private val checkInternetUseCase: CheckInternetUseCase
) : ViewModel() {
    private var _signUpResult: MutableLiveData<SignUpResult> = MutableLiveData()
    val signUpResult: LiveData<SignUpResult> = _signUpResult

    fun onRegisterClick(email: String, password: String) {
        viewModelScope.launch {
            _signUpResult.value = registerUseCase(email, password)
        }
    }

    @Throws(InterruptedException::class, IOException::class)
    fun onCheckInternet() : Boolean {
        var flag = false
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
