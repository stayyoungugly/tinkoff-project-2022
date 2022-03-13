package com.itis.springpractice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.itis.springpractice.databinding.FragmentSignInBinding
import com.itis.springpractice.network.client.UserAuthClient
import com.itis.springpractice.network.client.UserTokenClient
import com.itis.springpractice.network.repositories.UserAuthRepository
import com.itis.springpractice.network.repositories.UserTokenRepository
import com.itis.springpractice.network.responses.SignInResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var signInResponse: SignInResponse
    private var userTokenRepository: UserTokenRepository
    private var userAuthRepository: UserAuthRepository
    private var userAuthClient: UserAuthClient = UserAuthClient()
    private var userTokenClient: UserTokenClient = UserTokenClient()

    init {
        userTokenRepository = UserTokenRepository(userTokenClient.returnApi())
        userAuthRepository = UserAuthRepository(userAuthClient.returnApi())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val login = binding.etLogin.text.toString()
        val password = binding.etPassword.text.toString()
        lifecycleScope.launch {
            try {
                login(login, password)
                saveToken()
            } catch (ex: HttpException) {
                Timber.e(ex.message.toString())
            }
        }
    }

    private suspend fun login(login: String, password: String) {
        signInResponse = userAuthRepository.login(login, password)
    }

    private suspend fun saveToken() {
        val token = signInResponse.idToken
        userTokenRepository.saveToken(token)
    }
}
