package com.itis.springpractice.presentation.fragment

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.itis.springpractice.R
import com.itis.springpractice.data.api.firebase.FirebaseAuthApi
import com.itis.springpractice.data.api.firebase.FirebaseTokenApi
import com.itis.springpractice.data.api.mapper.SignInMapper
import com.itis.springpractice.data.api.mapper.SignUpMapper
import com.itis.springpractice.data.api.mapper.TokenMapper
import com.itis.springpractice.data.impl.UserAuthRepositoryImpl
import com.itis.springpractice.data.impl.UserTokenRepositoryImpl
import com.itis.springpractice.databinding.FragmentSignUpBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.SignInEntity
import com.itis.springpractice.domain.entity.SignUpEntity
import com.itis.springpractice.domain.repository.UserAuthRepository
import com.itis.springpractice.domain.repository.UserTokenRepository
import com.itis.springpractice.presentation.validation.RegistrationValidator
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var signUpEntity: SignUpEntity
    private lateinit var signInMapper: SignInMapper
    private lateinit var signUpMapper: SignUpMapper
    private lateinit var tokenMapper: TokenMapper
    private lateinit var userTokenRepository: UserTokenRepository
    private lateinit var userAuthRepository: UserAuthRepository
    private lateinit var registrationValidator: RegistrationValidator
    private lateinit var apiAuth: FirebaseAuthApi
    private lateinit var apiToken: FirebaseTokenApi


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
        initObjects()
        clickableText()
        binding.btnSignUp.setOnClickListener {
            val login = binding.etLogin.text.toString()
            val password = binding.etPassword.text.toString()
            lifecycleScope.launch {
                try {
                    register(login, password)
                } catch (ex: HttpException) {
                    Timber.e(ex.message.toString())
                }
            }
        }
    }

    private suspend fun saveToken() {
        val token = signUpEntity.idToken
        if (token != null) {
            userTokenRepository.saveToken(token)
        }
    }

    private suspend fun register(login: String, password: String) {
        signUpEntity = userAuthRepository.register(login, password)
        saveToken()
        findNavController().navigate(R.id.action_signUpFragment_to_profileFragment)
    }

    private fun clickableText() {
        val clickString = SpannableString(resources.getString(R.string.to_login))
        // Set clickable span
        clickString[18 until clickString.length] = object : ClickableSpan() {
            override fun onClick(view: View) {
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
        }
        binding.tvSignIn.movementMethod = LinkMovementMethod()
        binding.tvSignIn.text = clickString
    }

    private fun initObjects() {
        signInMapper = SignInMapper()
        signUpMapper = SignUpMapper()
        tokenMapper = TokenMapper()
        registrationValidator = RegistrationValidator()
        apiAuth = UserAuthContainer.api
        apiToken = UserTokenContainer.api
        userTokenRepository = UserTokenRepositoryImpl(apiToken, tokenMapper)
        userAuthRepository = UserAuthRepositoryImpl(apiAuth, signUpMapper, signInMapper)

    }
}
