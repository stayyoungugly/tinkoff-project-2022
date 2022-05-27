package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentVerifyEmailBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.UserInfoError
import com.itis.springpractice.domain.entity.UserInfoSuccess
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.VerifyEmailViewModel
import timber.log.Timber

class VerifyEmailFragment : Fragment(R.layout.fragment_verify_email) {
    private val binding by viewBinding(FragmentVerifyEmailBinding::bind)

    private val verifyEmailViewModel by viewModels<VerifyEmailViewModel> {
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

        verifyEmailViewModel.onSendVerificationClick()

        binding.btnVerify.setOnClickListener {
            verifyEmailViewModel.onGetUserInfoClick()
        }
        binding.btnSendAgain.setOnClickListener {
            verifyEmailViewModel.onSendVerificationClick()
        }
        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.action_verifyEmailFragment_to_authorizedFragment)
        }
    }

    private fun initObservers() {
        verifyEmailViewModel.errorModel.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                when (it.message) {
                    "INVALID_ID_TOKEN" -> showMessage("Ошибка запроса, попробуйте еще раз")
                    "USER_NOT_FOUND" -> showMessage("Пользователь не найден")
                    "TOO_MANY_ATTEMPTS_TRY_LATER" -> showMessage("Слишком много попыток. Попробуйте позже")
                    else -> showMessage("Ошибка отправки")
                }
            }, onFailure = {
                Timber.e(it.message.toString())
            })
        }
        verifyEmailViewModel.userInfoResult.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                when (it) {
                    is UserInfoSuccess -> {
                        if (it.emailVerified) {
                            findNavController().navigate(R.id.action_verifyEmailFragment_to_authorizedFragment)
                        } else showMessage("Почта не подтверждена. Повторите попытку")
                    }
                    is UserInfoError -> {
                        when (it.reason) {
                            "EXPIRED_OOB_CODE" -> showMessage("Код устарел, отправьте снова")
                            "INVALID_OOB_CODE" -> showMessage("Неправильный код")
                            "USER_DISABLED" -> showMessage("Вход для данного пользователя заблокирован")
                            "EMAIL_NOT_FOUND" -> showMessage("Такой почты не существует")
                            else -> showMessage("Ошибка регистрации")
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
}
