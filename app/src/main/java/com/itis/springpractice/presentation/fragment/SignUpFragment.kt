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
import com.itis.springpractice.data.api.mapper.*
import com.itis.springpractice.data.database.token.TokenDao
import com.itis.springpractice.data.database.token.TokenDatabase
import com.itis.springpractice.data.impl.UserAuthRepositoryImpl
import com.itis.springpractice.data.impl.UserTokenRepositoryImpl
import com.itis.springpractice.databinding.FragmentSignUpBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
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
    private lateinit var errorMapper: ErrorMapper
    private lateinit var tokenDatabase: TokenDatabase
    private lateinit var tokenDao: TokenDao
    private lateinit var verificationMapper: VerificationMapper
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
            if (registrationValidator.isValidEmail(login) ||
                registrationValidator.isValidPassword(password)
            ) {
                lifecycleScope.launch {
                    try {
                        register(login, password)
                    } catch (ex: HttpException) {
                        Timber.e(ex.message.toString())
                    }
                }
            } else if (!registrationValidator.isValidEmail(login)) {
                showMessage("Введите корректный Email")
            } else if (!registrationValidator.isValidPassword(password)) {
                showMessage("Пароль слишком легкий. Используйте цифры и буквы")
            }
        }
    }

    private suspend fun register(login: String, password: String) {
        signUpEntity = userAuthRepository.register(login, password)
        if (signUpEntity.errorMessage.isNullOrEmpty()) {
            val action = signUpEntity.idToken?.let {
                SignUpFragmentDirections.actionSignUpFragmentToVerifyEmailFragment(
                    it
                )
            }
            if (action != null) {
                if (sendVerification()) {
                    findNavController().navigate(action)
                }
            }
        } else {
            when (signUpEntity.errorMessage) {
                "EMAIL_EXISTS" -> showMessage("Пользователь с таким Email уже существует")
                "OPERATION_NOT_ALLOWED" -> showMessage("Операция недоступна")
                "TOO_MANY_ATTEMPTS_TRY_LATER" -> showMessage("Слишком много попыток, попробуйте позже")
                else -> showMessage("Ошибка регистрации")
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
        val token = signUpEntity.idToken
        if (token != null) {
            val errorEntity = userAuthRepository.sendVerification(token)
            if (errorEntity.message.equals("OK")) return true
            when (errorEntity.message) {
                "INVALID_ID_TOKEN" -> showMessage("Ошибка запроса, попробуйте еще раз")
                "USER_NOT_FOUND" -> showMessage("Пользователь не найден")
                else -> showMessage("Ошибка отправки")
            }
            return false
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
        binding.tvSignIn.movementMethod = LinkMovementMethod()
        binding.tvSignIn.text = clickString
    }

    private suspend fun saveToken() {
        val token = signUpEntity.idToken
        if (token != null) {
            userTokenRepository.saveToken(token)
        }
    }

    private fun initObjects() {
        signInMapper = SignInMapper()
        signUpMapper = SignUpMapper()
        tokenMapper = TokenMapper()
        errorMapper = ErrorMapper()
        verificationMapper = VerificationMapper()
        tokenDatabase = TokenDatabase.getInstance(this.requireContext())
        tokenDao = tokenDatabase.tokenDao()
        registrationValidator = RegistrationValidator()
        apiAuth = UserAuthContainer.api
        apiToken = UserTokenContainer.api
        userTokenRepository = UserTokenRepositoryImpl(apiToken, tokenMapper, tokenDao)
        userAuthRepository = UserAuthRepositoryImpl(
            apiAuth,
            signUpMapper,
            signInMapper,
            errorMapper,
            verificationMapper
        )
    }
}
