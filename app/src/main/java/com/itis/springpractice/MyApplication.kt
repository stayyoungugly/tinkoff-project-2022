package com.itis.springpractice

import android.app.Application
import android.content.Context
import com.yandex.mapkit.MapKitFactory

private const val MAP_KEY = BuildConfig.MAP_KEY

class MyApplication : Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        MapKitFactory.setApiKey(MAP_KEY)
    }
}
