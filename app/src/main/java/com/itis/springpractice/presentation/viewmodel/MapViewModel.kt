package com.itis.springpractice.presentation.viewmodel

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.MyApplication
import kotlinx.coroutines.launch

class MapViewModel(
) : ViewModel() {

    fun isPermissionsAllowed(): Boolean {
        var flag = false
        viewModelScope.launch {
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
        }
        return flag
    }
}
