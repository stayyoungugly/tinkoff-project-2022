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
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.data.api.firebase.FirebaseAuthApi
import com.itis.springpractice.data.api.firebase.FirebaseTokenApi
import com.itis.springpractice.data.api.mapper.SignInMapper
import com.itis.springpractice.data.api.mapper.SignUpMapper
import com.itis.springpractice.data.api.mapper.TokenMapper
import com.itis.springpractice.data.impl.UserAuthRepositoryImpl
import com.itis.springpractice.data.impl.UserTokenRepositoryImpl
import com.itis.springpractice.databinding.FragmentSignInBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.SignInEntity
import com.itis.springpractice.domain.repository.UserAuthRepository
import com.itis.springpractice.domain.repository.UserTokenRepository
import com.itis.springpractice.presentation.validation.RegistrationValidator
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var signInEntity: SignInEntity
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
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObjects()
        clickableText()
        binding.btnLogin.setOnClickListener {
            val login = binding.etLogin.text.toString()
            val password = binding.etPassword.text.toString()
            if (
                registrationValidator.isValidEmail(login) ||
                registrationValidator.isValidPassword(password)
            ) {
                lifecycleScope.launch {
                    try {
                        login(login, password)
                    } catch (ex: HttpException) {
                        Timber.e(ex.message.toString())
                    }
                }
            } else if (!registrationValidator.isValidEmail(login)) {
                showMessage("Email неверный")
            } else if (!registrationValidator.isValidPassword(password)) {
                showMessage("Пароль неверный")
            }
        }
    }

    private suspend fun login(login: String, password: String) {
        signInEntity = userAuthRepository.login(login, password)
        if (signInEntity.errorMessage.isNullOrEmpty()) {
            saveToken()
            println(signInEntity.email)
            println(signInEntity.errorMessage)
            findNavController().navigate(R.id.action_signInFragment_to_profileFragment)
        }
        else {
            when (signInEntity.errorMessage) {
                "EMAIL_NOT_FOUND" -> showMessage("Email не найден")
                "INVALID_PASSWORD" -> showMessage("Неверный пароль")
                "USER_DISABLED" -> showMessage("Доступ запрещен")
                else -> showMessage("Ошибка входа")
            }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private suspend fun saveToken() {
        val token = signInEntity.idToken
        if (token != null) {
            userTokenRepository.saveToken(token)
        }
    }

    private fun clickableText() {
        val clickString = SpannableString(resources.getString(R.string.to_sign_up))
        // Set clickable span
        clickString[19 until clickString.length] = object : ClickableSpan() {
            override fun onClick(view: View) {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }
        }
        binding.tvSignUp.movementMethod = LinkMovementMethod()
        binding.tvSignUp.text = clickString
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
