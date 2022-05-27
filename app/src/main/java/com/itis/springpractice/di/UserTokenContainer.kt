package com.itis.springpractice.di

import android.content.SharedPreferences
import com.itis.springpractice.BuildConfig
import com.itis.springpractice.data.api.firebase.FirebaseTokenApi
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.impl.UserTokenRepositoryImpl
import com.itis.springpractice.data.mapper.TokenMapper
import com.itis.springpractice.domain.repository.UserTokenRepository
import com.itis.springpractice.domain.usecase.token.*
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://securetoken.googleapis.com/v1/"
private const val API_KEY = BuildConfig.SECRET_KEY
private const val QUERY_API_KEY = "key"
private const val TYPE_HEADER = "Content-Type"
private const val FORM_TYPE = "application/x-www-form-urlencoded"

class UserTokenContainer(
    sharedPreferences: SharedPreferences
) {

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
                    TYPE_HEADER, FORM_TYPE
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

    val api: FirebaseTokenApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirebaseTokenApi::class.java)
    }

    private val userTokenRepository: UserTokenRepository = UserTokenRepositoryImpl(
        api = api,
        mapper = TokenMapper(),
        preferenceManager = PreferenceManager(sharedPreferences)
    )

    val saveTokenUseCase: SaveTokenUseCase = SaveTokenUseCase(
        userTokenRepository = userTokenRepository,
        dispatcher = Dispatchers.Default
    )

    val getTokenUseCase: GetTokenUseCase = GetTokenUseCase(
        userTokenRepository = userTokenRepository,
        dispatcher = Dispatchers.Default
    )

    val getRefreshTokenUseCase: GetRefreshTokenUseCase = GetRefreshTokenUseCase(
        userTokenRepository = userTokenRepository,
        dispatcher = Dispatchers.Default
    )

    val saveRefreshTokenUseCase: SaveRefreshTokenUseCase = SaveRefreshTokenUseCase(
        userTokenRepository = userTokenRepository,
        dispatcher = Dispatchers.Default
    )

    val refreshTokenUseCase: RefreshTokenUseCase = RefreshTokenUseCase(
        userTokenRepository = userTokenRepository,
        dispatcher = Dispatchers.Default
    )

    val deleteTokenUseCase: DeleteTokenUseCase = DeleteTokenUseCase(
        userTokenRepository = userTokenRepository,
        dispatcher = Dispatchers.Default
    )
}
