package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.content.SharedPreferences
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
import com.itis.springpractice.data.api.mapper.*
import com.itis.springpractice.data.impl.UserAuthRepositoryImpl
import com.itis.springpractice.data.impl.UserTokenRepositoryImpl
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.databinding.FragmentSignInBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.SignInError
import com.itis.springpractice.domain.entity.SignInResult
import com.itis.springpractice.domain.entity.SignInSuccess
import com.itis.springpractice.domain.repository.UserAuthRepository
import com.itis.springpractice.domain.repository.UserTokenRepository
import com.itis.springpractice.presentation.ui.validation.RegistrationValidator
import kotlinx.coroutines.*
import retrofit2.HttpException
import timber.log.Timber

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var signInResult: SignInResult
    private lateinit var signInMapper: SignInMapper
    private lateinit var signUpMapper: SignUpMapper
    private lateinit var tokenMapper: TokenMapper
    private lateinit var verificationMapper: VerificationMapper
    private lateinit var errorMapper: ErrorMapper
    private lateinit var userTokenRepository: UserTokenRepository
    private lateinit var userAuthRepository: UserAuthRepository
    private lateinit var registrationValidator: RegistrationValidator
    private lateinit var apiAuth: FirebaseAuthApi
    private lateinit var apiToken: FirebaseTokenApi
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var sharedPreferences: SharedPreferences

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
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        initObjects()
        clickableText()
        binding.btnLogin.setOnClickListener {
            val login = binding.etLogin.text.toString()
            val password = binding.etPassword.text.toString()
            if (registrationValidator.isValidEmail(login)) {
                lifecycleScope.launch {
                    try {
                        login(login, password)
                    } catch (ex: HttpException) {
                        Timber.e(ex.message.toString())
                    }
                }
            } else if (!registrationValidator.isValidEmail(login)) {
                showMessage("Введите корректный Email")
            }
        }
    }

    private suspend fun login(login: String, password: String) {
        signInResult = userAuthRepository.login(login, password)
        when (signInResult) {
            is SignInSuccess -> {
                saveToken()
                findNavController().navigate(R.id.action_signInFragment_to_profileFragment)
            }
            is SignInError -> {
                when ((signInResult as SignInError).reason) {
                    "EMAIL_NOT_FOUND" -> showMessage("Email не найден")
                    "INVALID_PASSWORD" -> showMessage("Неверный пароль")
                    "USER_DISABLED" -> showMessage("Доступ запрещен")
                    else -> showMessage("Ошибка входа")
                }
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
        (signInResult as SignInSuccess).idToken.let {
            userTokenRepository.saveToken(it)
        }
    }

    private fun clickableText() {
        val clickString = SpannableString(resources.getString(R.string.to_sign_up))
        clickString[18 until clickString.length + 1] = object : ClickableSpan() {
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
        errorMapper = ErrorMapper()
        verificationMapper = VerificationMapper()
        registrationValidator = RegistrationValidator()
        apiAuth = UserAuthContainer.api
        apiToken = UserTokenContainer.api
        preferenceManager = PreferenceManager(sharedPreferences)
        userTokenRepository = UserTokenRepositoryImpl(apiToken, tokenMapper, preferenceManager)
        userAuthRepository = UserAuthRepositoryImpl(
            apiAuth,
            signUpMapper,
            signInMapper,
            errorMapper,
            verificationMapper
        )
    }
}
