package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.usecase.permission.CheckLocationPermissionUseCase
import com.itis.springpractice.domain.usecase.token.DeleteTokenUseCase
import com.itis.springpractice.presentation.ui.fragment.MapFragment
import kotlinx.coroutines.launch

class MapViewModel(
    private val deleteTokenUseCase: DeleteTokenUseCase,
    private val checkLocationPermissionUseCase: CheckLocationPermissionUseCase

) : ViewModel() {

    fun onDeleteTokenClick() {
        viewModelScope.launch {
            deleteTokenUseCase()
        }
    }

    fun checkLocationPermission(fragment: MapFragment): Boolean {
        var flag = false
        viewModelScope.launch {
            flag = checkLocationPermissionUseCase(fragment)
        }
        return flag
    }
}
