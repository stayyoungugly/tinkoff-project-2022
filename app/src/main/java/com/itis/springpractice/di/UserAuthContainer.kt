package com.itis.springpractice.di

import com.itis.springpractice.BuildConfig
import com.itis.springpractice.data.api.firebase.FirebaseAuthApi
import com.itis.springpractice.data.mapper.ErrorMapper
import com.itis.springpractice.data.mapper.SignInMapper
import com.itis.springpractice.data.mapper.SignUpMapper
import com.itis.springpractice.data.mapper.UserInfoMapper
import com.itis.springpractice.data.impl.UserAuthRepositoryImpl
import com.itis.springpractice.domain.repository.UserAuthRepository
import com.itis.springpractice.domain.usecase.auth.*
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://identitytoolkit.googleapis.com/v1/"
private const val API_KEY = BuildConfig.SECRET_KEY
private const val QUERY_API_KEY = "key"
private const val TYPE_HEADER = "Content-Type"
private const val JSON_TYPE = "application/json"

object UserAuthContainer {
    
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

    val api: FirebaseAuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirebaseAuthApi::class.java)
    }

    private val userAuthRepository: UserAuthRepository = UserAuthRepositoryImpl(
        api = api,
        registerMapper = SignUpMapper(),
        loginMapper = SignInMapper(),
        errorMapper = ErrorMapper(),
        userInfoMapper = UserInfoMapper()
    )
    val loginUseCase: LoginUseCase = LoginUseCase(
        userAuthRepository = userAuthRepository,
        dispatcher = Dispatchers.Default
    )

    val registerUseCase: RegisterUseCase = RegisterUseCase(
        userAuthRepository = userAuthRepository,
        dispatcher = Dispatchers.Default
    )

    val sendVerificationUseCase: SendVerificationUseCase = SendVerificationUseCase(
        userAuthRepository = userAuthRepository,
        dispatcher = Dispatchers.Default
    )

    val getUserInfoUseCase: GetUserInfoUseCase = GetUserInfoUseCase(
        userAuthRepository = userAuthRepository,
        dispatcher = Dispatchers.Default
    )

    val deleteUserUseCase: DeleteUserUseCase = DeleteUserUseCase(
        userAuthRepository = userAuthRepository,
        dispatcher = Dispatchers.Default
    )
}
