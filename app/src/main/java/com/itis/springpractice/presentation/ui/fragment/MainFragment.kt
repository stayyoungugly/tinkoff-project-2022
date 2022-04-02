package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.MainViewModel
import com.itis.springpractice.presentation.viewmodel.VerifyEmailViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var token: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mainViewModel: MainViewModel

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
        initObservers()
        mainViewModel.onGetTokenClick()
    }

    private fun initObjects() {
        val factory = AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences)
        )
        mainViewModel = ViewModelProvider(
            this,
            factory
        )[MainViewModel::class.java]
    }

    private fun initObservers() {
        mainViewModel.token.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                token = it
                if (token.isNotEmpty()) {
                    findNavController().navigate(R.id.action_mainFragment_to_profileFragment)
                } else {
                    findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
                }
            }, onFailure = {
                Timber.e(it.message.toString())
            })
        }
    }
}
