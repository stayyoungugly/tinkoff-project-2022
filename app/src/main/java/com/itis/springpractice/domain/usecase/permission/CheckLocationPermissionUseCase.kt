package com.itis.springpractice.domain.usecase.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.itis.springpractice.MyApplication

class CheckLocationPermissionUseCase {
    private lateinit var fragment: Fragment
    private var flag = false

    suspend operator fun invoke(mapFragment: Fragment): Boolean {
        fragment = mapFragment
        return isPermissionsAllowed(MyApplication.appContext)
    }

    private fun isPermissionsAllowed(context: Context): Boolean {
        val permissionLocation =
            context.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        return if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            getLocationPermissions()
            flag
        } else {
            true
        }
    }

    private fun getLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private val locationPermissionRequest = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        flag = when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                true
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                true
            }
            else -> {
                false
            }
        }
    }
}
