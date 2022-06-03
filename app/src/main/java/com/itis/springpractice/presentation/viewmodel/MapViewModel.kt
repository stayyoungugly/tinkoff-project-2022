package com.itis.springpractice.presentation.viewmodel

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.MyApplication
import com.itis.springpractice.domain.usecase.token.DeleteTokenUseCase
import com.itis.springpractice.domain.usecase.user.DeleteNicknameUseCase
import kotlinx.coroutines.launch

class MapViewModel(
    private val deleteTokenUseCase: DeleteTokenUseCase,
    private val deleteNicknameUseCase: DeleteNicknameUseCase
) : ViewModel() {

    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

    fun onDeleteClick() {
        viewModelScope.launch {
            try {
                deleteTokenUseCase()
                deleteNicknameUseCase()
            } catch (ex: Exception) {
                _error.value = ex
            }
        }
    }

    fun isPermissionsAllowed(): Boolean {
        var flag = false
        viewModelScope.launch {
            try {
                MyApplication.appContext.let {
                    flag = (ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED)
                }
            } catch (ex: Exception) {
                _error.value = ex
            }
        }
        return flag
    }
}
