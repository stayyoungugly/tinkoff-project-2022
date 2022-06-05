package com.itis.springpractice.di

import com.itis.springpractice.BuildConfig
import com.itis.springpractice.data.api.firebase.FirebaseAuthApi
import com.itis.springpractice.data.impl.UserAuthRepositoryImpl
import com.itis.springpractice.domain.repository.UserAuthRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://identitytoolkit.googleapis.com/v1/"
private const val API_KEY = BuildConfig.SECRET_KEY
private const val QUERY_API_KEY = "key"
private const val TYPE_HEADER = "Content-Type"
private const val JSON_TYPE = "application/json"

val userAuthModule = module {

    factory(named("Auth")) {
        OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor())
            .addInterceptor(typeHeaderInterceptor())
            .also {
                if (BuildConfig.DEBUG) {
                    it.addInterceptor(
                        HttpLoggingInterceptor()
                            .setLevel(
                                HttpLoggingInterceptor.Level.BODY
                            )
                    )
                }
            }
            .build()
    }

    factory(named("AuthRetrofit")) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get(named("Auth")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirebaseAuthApi::class.java)
    }

    factory<UserAuthRepository> {
        (UserAuthRepositoryImpl(
            get(named("AuthRetrofit")),
            get(),
            get(),
            get(),
            get()
        ))
    }
}

private fun apiKeyInterceptor() = Interceptor { chain ->
    val original = chain.request()
    val newURL = original.url.newBuilder()
        .addQueryParameter(QUERY_API_KEY, API_KEY)
        .build()
    chain.proceed(
        original.newBuilder()
            .url(newURL)
            .build()
    )
}

private fun typeHeaderInterceptor() = Interceptor { chain ->
    val original = chain.request()
    chain.proceed(
        original.newBuilder()
            .header(
                TYPE_HEADER, JSON_TYPE
            )
            .build()
    )
}
