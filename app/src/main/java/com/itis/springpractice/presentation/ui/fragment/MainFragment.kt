package com.itis.springpractice.presentation.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.itis.springpractice.R
import com.itis.springpractice.presentation.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment(R.layout.fragment_main) {

    private val mainViewModel: MainViewModel by viewModel()

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
