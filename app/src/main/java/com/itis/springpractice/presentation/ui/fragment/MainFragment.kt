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
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentMainBinding
import com.itis.springpractice.di.PlaceContainer
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.MainViewModel

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
            UserTokenContainer(sharedPreferences),
            PlaceContainer
        )
        mainViewModel = ViewModelProvider(
            this,
            factory
        ).get(MainViewModel::class.java)
    }

    private fun initObservers() {
        mainViewModel.token.observe(viewLifecycleOwner) {
            token = it
            if (token.isNotEmpty()) {
                findNavController().navigate(R.id.action_mainFragment_to_mapFragment)
            } else {
                findNavController().navigate(R.id.action_mainFragment_to_signInFragment)
            }
        }
    }
}
