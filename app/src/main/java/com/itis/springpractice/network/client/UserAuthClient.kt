package com.itis.springpractice.network.client

import com.itis.springpractice.BuildConfig
import com.itis.springpractice.network.api.FirebaseAuthApi
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserAuthClient {

    companion object {
        private const val BASE_URL = "https://identitytoolkit.googleapis.com/v1/"
        private const val API_KEY = "AIzaSyBry_R7UWLJhRIgJigO6lqSvivAvUa3aDo"
        private const val QUERY_API_KEY = "key"
        private const val TYPE_HEADER = "Content-Type"
        private const val JSON_TYPE = "application/json"
    }

    private val apiKeyInterceptor = Interceptor { chain ->
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
    private val typeHeaderInterceptor = Interceptor { chain ->
        val original = chain.request()
        chain.proceed(
            original.newBuilder()
                .header(
                    TYPE_HEADER, JSON_TYPE
                )
                .build()
        )
    }


    private val okhttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(typeHeaderInterceptor)
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

    private val api: FirebaseAuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirebaseAuthApi::class.java)
    }

    fun returnApi(): FirebaseAuthApi {
        return api
    }

}