package com.itis.springpractice.di

import com.itis.springpractice.BuildConfig
import com.itis.springpractice.data.api.firebase.GeocodingApi
import com.itis.springpractice.data.api.mapper.PlaceHelperMapper
import com.itis.springpractice.data.impl.PlaceHelperRepositoryImpl
import com.itis.springpractice.domain.repository.PlaceHelperRepository
import com.itis.springpractice.domain.usecase.geocoding.GetPlaceIdUseCase
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://maps.googleapis.com/maps/api/geocode/"
private const val API_KEY = BuildConfig.SECRET_KEY
private const val QUERY_API_KEY = "key"

object PlaceContainer {
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

    private val okhttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
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

    val api: GeocodingApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeocodingApi::class.java)
    }

    private val placeHelperRepository: PlaceHelperRepository = PlaceHelperRepositoryImpl(
        api = api,
        placeHelperMapper = PlaceHelperMapper(),
    )

    val getPlaceIdUseCase: GetPlaceIdUseCase = GetPlaceIdUseCase(
        placeHelperRepository = placeHelperRepository,
        dispatcher = Dispatchers.Default
    )
}
