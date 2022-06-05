package com.itis.springpractice.presentation.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentAuthorizedBinding

interface Callbacks {
    fun navigateToSignIn()
    fun navigateToEdit()
}

class AuthorizedFragment : Fragment(R.layout.fragment_authorized), Callbacks {
    private val controller by lazy {
        (childFragmentManager.findFragmentById(R.id.authorized_nav_root) as NavHostFragment)
            .navController
    }
    private val binding by viewBinding(FragmentAuthorizedBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val drawer = binding.authorizedDrawer
        val appBarConfiguration = AppBarConfiguration(controller.graph, drawer)
        binding.authorizedToolbar.setupWithNavController(controller, appBarConfiguration)

        binding.navView.setupWithNavController(controller)
    }

    override fun navigateToSignIn() {
        findNavController().navigate(R.id.action_authorizedFragment_to_signInFragment)
    }

    override fun navigateToEdit() {
        findNavController().navigate(R.id.action_authorizedFragment_to_editProfileFragment)
    }
}
