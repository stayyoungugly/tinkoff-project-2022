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
import com.itis.springpractice.databinding.FragmentSignUpBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.SignUpError
import com.itis.springpractice.domain.entity.SignUpSuccess
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.ui.validation.RegistrationValidator
import com.itis.springpractice.presentation.viewmodel.SignUpViewModel


class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    private val binding by viewBinding(FragmentSignUpBinding::bind)
    private lateinit var nickname: String
    private lateinit var firstName: String
    private lateinit var lastName: String
    private lateinit var password: String
    private lateinit var login: String

    private val signUpViewModel by viewModels<SignUpViewModel> {
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
        clickableText()
        binding.authFields.btnNext.setOnClickListener {
            register()
        }
    }

    private fun register() {
        with(binding) {
            login = authFields.etLogin.text.toString()
            password = authFields.etPassword.text.toString()
            firstName = authFields.etFirstName.text.toString()
            lastName = authFields.etLastName.text.toString()
            nickname = authFields.etNickname.text.toString()
            val checkPassword = authFields.etPasswordCheck.text.toString()

            if (registrationValidator.isValidEmail(login) &&
                registrationValidator.isValidPassword(password) &&
                registrationValidator.isValidName(firstName) &&
                registrationValidator.isValidName(lastName) &&
                registrationValidator.isValidNickname(nickname)
            ) {
                if (password == checkPassword) {
                    signUpViewModel.isNicknameAvailable(nickname)
                } else showMessage(resources.getString(R.string.check_password_error))
            } else when {
                !registrationValidator.isValidEmail(login) -> showMessage(resources.getString(R.string.email_error))
                !registrationValidator.isValidPassword(password) -> showMessage(resources.getString(R.string.password_error))
                !registrationValidator.isValidName(firstName) -> showMessage(resources.getString(R.string.first_name_error))
                !registrationValidator.isValidName(lastName) -> showMessage(resources.getString(R.string.last_name_error))
                !registrationValidator.isValidNickname(nickname) -> showMessage(resources.getString(R.string.nickname_error))
                else -> showMessage(resources.getString(R.string.sign_up_error))
            }
        }
    }

    private fun initObservers() {
        signUpViewModel.nicknameAvailable.observe(viewLifecycleOwner) {
            if (it) {
                signUpViewModel.onRegisterClick(login, password)
            } else showMessage(resources.getString(R.string.nickname_exists))
        }

        signUpViewModel.signUpResult.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                when (it) {
                    is SignUpSuccess -> {
                        findNavController().navigate(R.id.action_signUpFragment_to_verifyEmailFragment)
                        signUpViewModel.onSaveTokenClick(it.idToken)
                        signUpViewModel.addNewUser(firstName, lastName, nickname)
                    }
                    is SignUpError -> {
                        when (it.reason) {
                            EMAIL_EXISTS -> showMessage(resources.getString(R.string.email_exists))
                            OPERATION_NOT_ALLOWED -> showMessage(resources.getString(R.string.operation_not_allowed))
                            TOO_MANY_ATTEMPTS_TRY_LATER -> showMessage(resources.getString(R.string.too_many_attempts))
                            else -> showMessage(resources.getString(R.string.sign_up_error))
                        }
                    }
                }
            }, onFailure = {
                showMessage(resources.getString(R.string.internet_error))
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
        val clickString = SpannableString(resources.getString(R.string.to_login))
        clickString[18 until clickString.length + 1] = object : ClickableSpan() {
            override fun onClick(view: View) {
                findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
            }
        }
        binding.authFields.tvElse.movementMethod = LinkMovementMethod()
        binding.authFields.tvElse.text = clickString
    }

    companion object {
        private const val EMAIL_EXISTS = "EMAIL_EXISTS"
        private const val OPERATION_NOT_ALLOWED = "OPERATION_NOT_ALLOWED"
        private const val TOO_MANY_ATTEMPTS_TRY_LATER = "TOO_MANY_ATTEMPTS_TRY_LATER"
    }
}
