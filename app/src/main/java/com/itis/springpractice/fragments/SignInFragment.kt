package com.itis.springpractice.fragments

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentSignInBinding
import com.itis.springpractice.network.client.UserAuthClient
import com.itis.springpractice.network.client.UserTokenClient
import com.itis.springpractice.network.repositories.UserAuthRepository
import com.itis.springpractice.network.repositories.UserTokenRepository
import com.itis.springpractice.network.responses.SignInResponse
import com.itis.springpractice.validation.RegistrationValidator
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
    private var registrationValidator: RegistrationValidator = RegistrationValidator()

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
        clickableText()
        binding.btnLogin.setOnClickListener {
            if (
                registrationValidator.isValidEmail(login) &&
                registrationValidator.isValidPassword(password)
            ) {
                lifecycleScope.launch {
                    try {
                        login(login, password)
                    } catch (ex: HttpException) {
                        Timber.e(ex.message.toString())
                    }
                }
            }
            else if (!registrationValidator.isValidEmail(login)) {
                showMessage("Email неверный")
            }
            else if (!registrationValidator.isValidPassword(password)) {
                showMessage("Пароль неверный")
            }
        }
    }

    private suspend fun login(login: String, password: String) {
        signInResponse = userAuthRepository.login(login, password)
        //TODO если ошибок нет, вызвать saveToken() и redirect
        //TODO иначе:
        when (signInResponse.error.errors.message) {
            "EMAIL_NOT_FOUND" -> showMessage("Email не найден")
            "INVALID_PASSWORD" -> showMessage("неверный пароль")
            "USER_DISABLED" -> showMessage("Доступ запрещен")
            else -> showMessage("Ошибка входа")
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
        val token = signInResponse.idToken
        userTokenRepository.saveToken(token)
    }

    private fun clickableText() {
        val clickString = SpannableString(resources.getString(R.string.to_sign_up))
        // Set clickable span
        clickString[18 until clickString.length] = object : ClickableSpan() {
            override fun onClick(view: View) {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }
        }
        binding.tvSignUp.movementMethod = LinkMovementMethod()
        binding.tvSignUp.text = clickString
    }
}
