package com.itis.springpractice.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import com.itis.springpractice.databinding.FragmentSignUpBinding
import com.itis.springpractice.network.client.UserAuthClient
import com.itis.springpractice.network.client.UserTokenClient
import com.itis.springpractice.network.repositories.UserAuthRepository
import com.itis.springpractice.network.repositories.UserTokenRepository
import com.itis.springpractice.network.responses.SignUpResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var signUpResponse: SignUpResponse
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
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val login = binding.etLogin.text.toString()
        val password = binding.etPassword.text.toString()

        binding.btnSignUp.setOnClickListener {
            lifecycleScope.launch {
                try {
                    signUp(login, password)
                    saveToken()
                } catch (ex: HttpException) {
                    Timber.e(ex.message.toString())
                }
            }
        }
    }

    private suspend fun signUp(login: String, password: String) {
        register(login, password)
    }


    private suspend fun saveToken() {
        val token = signUpResponse.idToken
        userTokenRepository.saveToken(token)
    }

    private suspend fun register(login: String, password: String) {
        signUpResponse = userAuthRepository.register(login, password)
    }
}
