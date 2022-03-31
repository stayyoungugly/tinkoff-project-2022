package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.data.api.firebase.FirebaseAuthApi
import com.itis.springpractice.data.api.firebase.FirebaseTokenApi
import com.itis.springpractice.data.api.mapper.*
import com.itis.springpractice.data.impl.UserAuthRepositoryImpl
import com.itis.springpractice.data.impl.UserTokenRepositoryImpl
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.databinding.FragmentVerifyEmailBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.UserInfoError
import com.itis.springpractice.domain.entity.UserInfoResult
import com.itis.springpractice.domain.entity.UserInfoSuccess
import com.itis.springpractice.domain.repository.UserAuthRepository
import com.itis.springpractice.domain.repository.UserTokenRepository
import com.itis.springpractice.presentation.ui.validation.RegistrationValidator
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class VerifyEmailFragment : Fragment() {
    private lateinit var binding: FragmentVerifyEmailBinding
    private lateinit var userInfoResult: UserInfoResult
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
    private lateinit var token: String
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var sharedPreferences: SharedPreferences

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
        binding.btnVerify.setOnClickListener {
            lifecycleScope.launch {
                try {
                    acceptVerification()
                } catch (ex: HttpException) {
                    Timber.e(ex.message.toString())
                }
            }
        }
        binding.btnSendAgain.setOnClickListener {
            lifecycleScope.launch {
                try {
                    sendVerification()
                } catch (ex: HttpException) {
                    Timber.e(ex.message.toString())
                }
            }
        }
        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.action_verifyEmailFragment_to_profileFragment)
        }
    }

    private suspend fun acceptVerification() {
        token = userTokenRepository.getToken()
        userInfoResult = userAuthRepository.getUserInfo(token)
        when (userInfoResult) {
            is UserInfoSuccess -> {
                if ((userInfoResult as UserInfoSuccess).emailVerified) {
                    findNavController().navigate(R.id.action_verifyEmailFragment_to_profileFragment)
                } else showMessage("Почта не подтверждена. Повторите попытку")
            }
            is UserInfoError -> {
                when ((userInfoResult as UserInfoError).reason) {
                    "EXPIRED_OOB_CODE" -> showMessage("Код устарел, отправьте снова")
                    "INVALID_OOB_CODE" -> showMessage("Неправильный код")
                    "USER_DISABLED" -> showMessage("Вход для данного пользователя заблокирован")
                    "EMAIL_NOT_FOUND" -> showMessage("Такой почты не существует")
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
        val errorEntity = userAuthRepository.sendVerification(token)
        if (errorEntity.message == "OK") {
            return true
        }
        when (errorEntity.message) {
            "INVALID_ID_TOKEN" -> showMessage("Ошибка запроса, попробуйте еще раз")
            "USER_NOT_FOUND" -> showMessage("Пользователь не найден")
            else -> showMessage("Ошибка отправки")
        }
        return false
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
