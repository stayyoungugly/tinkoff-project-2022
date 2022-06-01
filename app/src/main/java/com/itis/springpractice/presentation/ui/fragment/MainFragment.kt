package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.itis.springpractice.R
import com.itis.springpractice.di.FriendContainer
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.MainViewModel

class MainFragment : Fragment(R.layout.fragment_main) {

    private val mainViewModel by viewModels<MainViewModel> {
        AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences),
            UserContainer(sharedPreferences),
            FriendContainer(sharedPreferences)
        )
    }

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        mainViewModel.onGetTokenClick()
    }

    private fun initObservers() {
        mainViewModel.token.observe(viewLifecycleOwner) {
            val token = it
            if (token.isNotEmpty()) {
                findNavController().navigate(R.id.action_mainFragment_to_authorizedFragment)
            } else {
                findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
            }
        }
    }
}
