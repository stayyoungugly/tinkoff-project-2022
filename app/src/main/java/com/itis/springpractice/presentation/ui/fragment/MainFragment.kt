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
import com.itis.springpractice.R
import com.itis.springpractice.data.api.firebase.FirebaseAuthApi
import com.itis.springpractice.data.api.firebase.FirebaseTokenApi
import com.itis.springpractice.data.api.mapper.*
import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.impl.UserTokenRepositoryImpl
import com.itis.springpractice.databinding.FragmentMainBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.repository.UserTokenRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var tokenMapper: TokenMapper
    private lateinit var userTokenRepository: UserTokenRepository
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
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        initObjects()
        lifecycleScope.launch {
            try {
                getToken()
            } catch (ex: HttpException) {
                Timber.e(ex.message.toString())
            }
        }
        selectDestination()
    }

    private fun selectDestination() {
        if (token.isNotEmpty()) {
            findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
        } else {
            findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
        }
    }

    private suspend fun getToken() {
        token = userTokenRepository.getToken()
    }

    private fun initObjects() {
        tokenMapper = TokenMapper()
        apiAuth = UserAuthContainer.api
        apiToken = UserTokenContainer.api
        preferenceManager = PreferenceManager(sharedPreferences)
        userTokenRepository = UserTokenRepositoryImpl(apiToken, tokenMapper, preferenceManager)
    }
}
