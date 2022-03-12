package com.itis.springpractice.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.itis.adefault.databinding.FragmentSignInBinding

class SignInFragment: Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private lateinit var signInResponse: SignInResponse

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

        val login = binding.etLogin
        val password = binding.etPassword
        lifecycleScope.launch {
            try {
                signInResponse = UserAuthRepository.login(login, password)
                val token = signInResponse.idToken
                UserTokenRepository.saveToken(token)
            } catch (ex: HttpException) {
                Timber.e(ex.message.toString())
            }
        }
    }
}
