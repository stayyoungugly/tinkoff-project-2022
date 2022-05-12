package com.itis.springpractice.domain.usecase.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.itis.springpractice.MyApplication
import timber.log.Timber
import java.io.IOException

class CheckInternetUseCase {
    @Throws(InterruptedException::class, IOException::class)
    suspend operator fun invoke(): Boolean {
        return isOnline(MyApplication.appContext)
    }

    @SuppressLint("MissingPermission")
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
                ?: return false
        val capabilities: NetworkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        with(capabilities) {
            when {
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Timber.e("NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                }
                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Timber.e("NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Timber.e("NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
                else -> {
                    return false
                }
            }
        }
    }
}
