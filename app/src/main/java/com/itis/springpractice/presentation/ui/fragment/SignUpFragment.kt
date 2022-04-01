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
import com.itis.springpractice.databinding.FragmentSignUpBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.*
import com.itis.springpractice.domain.repository.UserAuthRepository
import com.itis.springpractice.domain.repository.UserTokenRepository
import com.itis.springpractice.presentation.ui.validation.RegistrationValidator
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var signUpResult: SignUpResult
    private lateinit var signInMapper: SignInMapper
    private lateinit var signUpMapper: SignUpMapper
    private lateinit var tokenMapper: TokenMapper
    private lateinit var errorMapper: ErrorMapper
    private lateinit var userInfoMapper: UserInfoMapper
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
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        initObjects()
        clickableText()
        binding.authFields.btnNext.setOnClickListener {
            val login = binding.authFields.etLogin.text.toString()
            val password = binding.authFields.etPassword.text.toString()
            val checkPassword = binding.authFields.etPasswordCheck.text.toString()
            if (registrationValidator.isValidEmail(login) &&
                registrationValidator.isValidPassword(password)
            ) {
                if (password == checkPassword) {
                    lifecycleScope.launch {
                        try {
                            register(login, password)
                        } catch (ex: HttpException) {
                            Timber.e(ex.message.toString())
                        }
                    }
                } else showMessage("Пароли не совпадают")
            } else if (!registrationValidator.isValidEmail(login)) {
                showMessage("Введите корректный Email")
            } else if (!registrationValidator.isValidPassword(password)) {
                showMessage("Пароль должен состоять минимум из 8 символов, минимум одна буква и одна цифра")
            }
        }
    }

    private suspend fun register(login: String, password: String) {
        signUpResult = userAuthRepository.register(login, password)
        when (signUpResult) {
            is SignUpSuccess -> {
                if (sendVerification()) {
                    findNavController().navigate(R.id.action_signUpFragment_to_verifyEmailFragment)
                    saveToken()
                }
            }
            is SignUpError -> {
                when ((signUpResult as SignUpError).reason) {
                    "EMAIL_EXISTS" -> showMessage("Пользователь с таким Email уже существует")
                    "OPERATION_NOT_ALLOWED" -> showMessage("Операция недоступна")
                    "TOO_MANY_ATTEMPTS_TRY_LATER" -> showMessage("Слишком много попыток, попробуйте позже")
                    else -> showMessage("Ошибка регистрации")
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

    private suspend fun sendVerification(): Boolean {
        val token = (signUpResult as SignUpSuccess).idToken
        val errorEntity = userAuthRepository.sendVerification(token)
        if (errorEntity.message == "OK") return true
        when (errorEntity.message) {
            "INVALID_ID_TOKEN" -> showMessage("Ошибка запроса, попробуйте еще раз")
            "USER_NOT_FOUND" -> showMessage("Пользователь не найден")
            else -> showMessage("Ошибка отправки")
        }
        return false
    }

    private fun clickableText() {
        val clickString = SpannableString(resources.getString(R.string.to_login))
        clickString[18 until clickString.length + 1] = object : ClickableSpan() {
            override fun onClick(view: View) {
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
        }
        binding.authFields.tvElse.movementMethod = LinkMovementMethod()
        binding.authFields.tvElse.text = clickString
    }

    private suspend fun saveToken() {
        (signUpResult as SignUpSuccess).idToken.let {
            userTokenRepository.saveToken(it)
        }
    }

    private fun initObjects() {
        signInMapper = SignInMapper()
        signUpMapper = SignUpMapper()
        tokenMapper = TokenMapper()
        errorMapper = ErrorMapper()
        userInfoMapper = UserInfoMapper()
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
            userInfoMapper
        )
    }
}