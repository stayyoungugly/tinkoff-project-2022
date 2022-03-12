package com.itis.springpractice.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.itis.adefault.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var signUpResponse: SignUpResponse

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

        val login = binding.etLogin
        val password = binding.etPassword

        binding.btnLogIn.setOnClickListener {
            signUp()
        }
        binding.btnSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_sign_up_to_fragment_sign_in)
        }
    }

    private fun signUp() {
        lifecycleScope.launch {
            try {
                signUpResponse = UserAuthRepository.register(login, password)
                val token = signUpResponse.idToken
                UserTokenRepository.saveToken(token)
            } catch (ex: HttpException) {
                Timber.e(ex.message.toString())
            }
        }
    }
}
