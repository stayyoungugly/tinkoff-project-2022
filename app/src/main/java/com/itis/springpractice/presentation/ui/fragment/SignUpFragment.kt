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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentSignUpBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.SignUpError
import com.itis.springpractice.domain.entity.SignUpSuccess
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.ui.validation.RegistrationValidator
import com.itis.springpractice.presentation.viewmodel.SignUpViewModel

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var signUpViewModel: SignUpViewModel

    private val registrationValidator by lazy {
        RegistrationValidator()
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
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        initObjects()
        initObservers()
        clickableText()
        binding.authFields.btnNext.setOnClickListener {
            val login = binding.authFields.etLogin.text.toString()
            val password = binding.authFields.etPassword.text.toString()
            val checkPassword = binding.authFields.etPasswordCheck.text.toString()
            if (registrationValidator.isValidEmail(login) &&
                registrationValidator.isValidPassword(password)
            ) {
                if (password == checkPassword) {
                    signUpViewModel.onRegisterClick(login, password)
                } else showMessage("Пароли не совпадают")
            } else if (!registrationValidator.isValidEmail(login)) {
                showMessage("Введите корректный Email")
            } else if (!registrationValidator.isValidPassword(password)) {
                showMessage("Пароль должен состоять из 6 символов, иметь одну букву и одну цифру")
            }
        }
    }

    private fun initObjects() {
        val factory = AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences)
        )
        signUpViewModel = ViewModelProvider(
            this,
            factory
        ).get(SignUpViewModel::class.java)
    }

    private fun initObservers() {
        signUpViewModel.signUpResult.observe(viewLifecycleOwner) {
            when (it) {
                is SignUpSuccess -> {
                    findNavController().navigate(R.id.action_signUpFragment_to_verifyEmailFragment)
                    signUpViewModel.onSaveTokenClick(it.idToken)
                }
                is SignUpError -> {
                    when (it.reason) {
                        "EMAIL_EXISTS" -> showMessage("Пользователь с таким Email уже существует")
                        "OPERATION_NOT_ALLOWED" -> showMessage("Операция недоступна")
                        "TOO_MANY_ATTEMPTS_TRY_LATER" -> showMessage("Слишком много попыток, попробуйте позже")
                        else -> showMessage("Ошибка регистрации")
                    }
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
}
