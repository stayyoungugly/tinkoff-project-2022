package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentVerifyEmailBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.UserInfoError
import com.itis.springpractice.domain.entity.UserInfoSuccess
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.VerifyEmailViewModel
import timber.log.Timber

class VerifyEmailFragment : Fragment() {
    private lateinit var binding: FragmentVerifyEmailBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var verifyEmailViewModel: VerifyEmailViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVerifyEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        initObjects()
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

    private fun initObjects() {
        val factory = AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences)
        )
        verifyEmailViewModel = ViewModelProvider(
            this,
            factory
        ).get(VerifyEmailViewModel::class.java)
    }

    private fun initObservers() {
        verifyEmailViewModel.errorEntity.observe(viewLifecycleOwner) { result ->
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
                Timber.e(it.message.toString())
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
