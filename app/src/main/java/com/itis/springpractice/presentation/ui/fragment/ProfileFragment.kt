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
import com.itis.springpractice.databinding.FragmentProfileBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.MainViewModel
import com.itis.springpractice.presentation.viewmodel.ProfileViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        initObjects()
        binding.btnSignOut.setOnClickListener {
            profileViewModel.onDeleteTokenClick()
            findNavController().navigate(R.id.action_profileFragment_to_mainFragment)
        }
    }

    private fun initObjects() {
        val factory = AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences)
        )
        profileViewModel = ViewModelProvider(
            this,
            factory
        )[ProfileViewModel::class.java]
    }
}
