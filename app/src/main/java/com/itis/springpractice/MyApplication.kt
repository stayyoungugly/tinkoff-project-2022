package com.itis.springpractice

import android.app.Application
import android.content.Context
import com.itis.springpractice.di.*
import com.yandex.mapkit.MapKitFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

private const val MAP_KEY = BuildConfig.MAP_KEY

class MyApplication : Application() {

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        MapKitFactory.setApiKey(MAP_KEY)
        startKoin {
            allowOverride(false)
            androidContext(this@MyApplication)
            modules(
                listOf(
                    viewModelModule,
                    useCaseModule,
                    userAuthModule,
                    userTokenModule,
                    repositoryModule,
                    sharedPreferencesModule
                )
            )
        }
    }
}
