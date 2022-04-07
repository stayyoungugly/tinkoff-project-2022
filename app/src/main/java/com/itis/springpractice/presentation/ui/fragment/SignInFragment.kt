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
import com.itis.springpractice.databinding.FragmentSignInBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.SignInError
import com.itis.springpractice.domain.entity.SignInSuccess
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.ui.validation.RegistrationValidator
import com.itis.springpractice.presentation.viewmodel.SignInViewModel

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var signInViewModel: SignInViewModel

    private val registrationValidator by lazy {
        RegistrationValidator()
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
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        initObjects()
        initObservers()
        initViewParams()
        clickableText()
        binding.authFields.btnNext.setOnClickListener {
            val login = binding.authFields.etLogin.text.toString()
            val password = binding.authFields.etPassword.text.toString()
            if (registrationValidator.isValidEmail(login)) {
                signInViewModel.onLoginClick(login, password)
            } else {
                showMessage("Введите корректный Email")
            }
        }
    }

    private fun initObjects() {
        val factory = AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences)
        )
        signInViewModel = ViewModelProvider(
            this,
            factory
        ).get(SignInViewModel::class.java)
    }

    private fun initObservers() {
        signInViewModel.signInResult.observe(viewLifecycleOwner) {
            when (it) {
                is SignInSuccess -> {
                    signInViewModel.onSaveTokenClick(it.idToken)
                    findNavController().navigate(R.id.action_signInFragment_to_profileFragment)
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
            btnNext.text = getString(R.string.log_in)
            tvElse.text = getString(R.string.to_sign_up)
        }
    }
}
