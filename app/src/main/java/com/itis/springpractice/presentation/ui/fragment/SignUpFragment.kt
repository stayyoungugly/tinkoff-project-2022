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
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.SignUpViewModel


class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    private val binding by viewBinding(FragmentSignUpBinding::bind)

    private val signUpViewModel by viewModels<SignUpViewModel> {
        AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences),
            UserContainer(sharedPreferences)
        )
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
            val login = authFields.etLogin.text.toString()
            val password = authFields.etPassword.text.toString()
            val firstName = authFields.etFirstName.text.toString()
            val lastName = authFields.etLastName.text.toString()
            val nickname = authFields.etNickname.text.toString()
            val checkPassword = authFields.etPasswordCheck.text.toString()
            if (password == checkPassword) {
                signUpViewModel.onRegisterClick(login, password, firstName, lastName, nickname)
            } else showMessage(getString(R.string.check_password_error))
        }
    }

    private fun initObservers() {
        signUpViewModel.error.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                findNavController().navigate(R.id.action_signUpFragment_to_verifyEmailFragment)
            } else showMessage(it)
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
