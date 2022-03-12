package com.itis.springpractice.network.repositories

import com.itis.springpractice.network.api.FirebaseAuthApi
import com.itis.springpractice.network.client.UserAuthClient
import com.itis.springpractice.network.responses.SignInResponse
import com.itis.springpractice.network.responses.SignUpResponse

class UserAuthRepository(private var api: FirebaseAuthApi) {

    companion object {
        private const val secure = "true"
    }

    suspend fun login(email: String, password: String): SignInResponse {
        return api.login(email, password, secure)
    }

    suspend fun register(email: String, password: String): SignUpResponse {
        return api.register(email, password, secure)
    }
}
