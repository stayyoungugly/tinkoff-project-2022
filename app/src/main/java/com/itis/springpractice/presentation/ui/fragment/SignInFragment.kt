package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.text.set
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentSignInBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.SignInError
import com.itis.springpractice.domain.entity.SignInSuccess
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.ui.validation.RegistrationValidator
import com.itis.springpractice.presentation.viewmodel.SignInViewModel

class SignInFragment : Fragment(R.layout.fragment_sign_in) {
    private val binding by viewBinding(FragmentSignInBinding::bind)

    private val signInViewModel by viewModels<SignInViewModel> {
        AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences),
            UserContainer(sharedPreferences)
        )
    }

    private val registrationValidator by lazy {
        RegistrationValidator()
    }

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        initViewParams()
        clickableText()
        binding.authFields.btnNext.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val login = binding.authFields.etLogin.text.toString()
        val password = binding.authFields.etPassword.text.toString()
        if (registrationValidator.isValidEmail(login)) {
            signInViewModel.onLoginClick(login, password)
        } else {
            showMessage("Введите корректный Email")
        }
    }

    private fun initObservers() {
        signInViewModel.signInResult.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                when (it) {
                    is SignInSuccess -> {
                        signInViewModel.onSaveTokenClick(it.idToken)
                        findNavController().navigate(R.id.action_signInFragment_to_authorizedFragment)
                    }
                    is SignInError -> {
                        when (it.reason) {
                            "EMAIL_NOT_FOUND" -> showMessage("Email не найден")
                            "INVALID_PASSWORD" -> showMessage("Неверный пароль")
                            "USER_DISABLED" -> showMessage("Доступ запрещен")
                            else -> showMessage("Ошибка входа")
                        }
                    }
                }
            }, onFailure = {
                showMessage("Проверьте подключение к интернету")
            })
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
        val clickString = SpannableString(resources.getString(R.string.to_sign_up))
        clickString[18 until clickString.length + 1] = object : ClickableSpan() {
            override fun onClick(view: View) {
                findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
            }
        }
        binding.authFields.tvElse.movementMethod = LinkMovementMethod()
        binding.authFields.tvElse.text = clickString
    }

    private fun initViewParams() {
        with(binding.authFields) {
            tiPasswordCheck.visibility = View.GONE
            tiFirstName.visibility = View.GONE
            tiLastName.visibility = View.GONE
            tiNickname.visibility = View.GONE
            btnNext.text = getString(R.string.log_in)
            tvElse.text = getString(R.string.to_sign_up)
        }
    }
}
