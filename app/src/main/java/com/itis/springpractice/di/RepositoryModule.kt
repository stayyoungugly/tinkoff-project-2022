package com.itis.springpractice.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.impl.FriendsRepositoryImpl
import com.itis.springpractice.data.impl.ReviewRepositoryImpl
import com.itis.springpractice.data.impl.UserRepositoryImpl
import com.itis.springpractice.data.mapper.*
import com.itis.springpractice.domain.repository.FriendsRepository
import com.itis.springpractice.domain.repository.ReviewRepository
import com.itis.springpractice.domain.repository.UserRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val repositoryModule = module {
    factoryOf(::ReviewRepositoryImpl) { bind<ReviewRepository>() }
    factoryOf(::UserRepositoryImpl) { bind<UserRepository>() }
    factoryOf(::FriendsRepositoryImpl) { bind<FriendsRepository>() }

    factoryOf(::PreferenceManager)
    factoryOf(::Firestore)

    //mapper
    factoryOf(::ErrorMapper)
    factoryOf(::ReviewEntityMapper)
    factoryOf(::SignInMapper)
    factoryOf(::SignUpMapper)
    factoryOf(::TokenMapper)
    factoryOf(::UserInfoMapper)
    factoryOf(::UserModelMapper)

}

val sharedPreferencesModule = module {
    single { provideSharedPref(androidApplication()) }
}


fun provideSharedPref(app: Application): SharedPreferences {
    return app.applicationContext.getSharedPreferences(
        "shared_preferences",
        Context.MODE_PRIVATE
    )
}
