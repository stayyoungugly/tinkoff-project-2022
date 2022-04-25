package com.itis.springpractice.presentation.viewmodel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.usecase.token.DeleteTokenUseCase
import kotlinx.coroutines.launch

class MapViewModel(
    private val deleteTokenUseCase: DeleteTokenUseCase
) : ViewModel() {

    fun onDeleteTokenClick() {
        viewModelScope.launch {
            deleteTokenUseCase()
        }
    }

    private val _location: MutableLiveData<Location> = MutableLiveData()
    val location: LiveData<Location> = _location

    fun onPermissionResult(location: Location) {
        _location.value = location
    }
}
