package com.itis.springpractice.di

import com.itis.springpractice.BuildConfig
import com.itis.springpractice.data.api.firebase.FirebaseTokenApi
import com.itis.springpractice.data.impl.UserTokenRepositoryImpl
import com.itis.springpractice.domain.repository.UserTokenRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://securetoken.googleapis.com/v1/"
private const val API_KEY = BuildConfig.SECRET_KEY
private const val QUERY_API_KEY = "key"
private const val TYPE_HEADER = "Content-Type"
private const val FORM_TYPE = "application/x-www-form-urlencoded"

val userTokenModule = module {
    factory(named("Token")) {
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

    factory(named("TokenRetrofit")) {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(get(named("Token")))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirebaseTokenApi::class.java)
    }

    factory<UserTokenRepository> {
        (UserTokenRepositoryImpl(
            get(named("TokenRetrofit")),
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
                TYPE_HEADER, FORM_TYPE
            )
            .build()
    )
}
