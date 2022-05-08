package com.itis.springpractice.presentation.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentAuthorizedBinding

interface Callbacks {
    fun navigateToSignIn()
}

class AuthorizedFragment : Fragment(R.layout.fragment_authorized), Callbacks {
    private lateinit var controller: NavController
    private lateinit var binding: FragmentAuthorizedBinding
    private lateinit var drawer: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAuthorizedBinding.bind(view)

        controller =
            (childFragmentManager.findFragmentById(R.id.authorized_nav_root) as NavHostFragment)
                .navController

        drawer = binding.authorizedDrawer
        appBarConfiguration = AppBarConfiguration(controller.graph, drawer)
        binding.authorizedToolbar.setupWithNavController(controller, appBarConfiguration)

        binding.navView.setupWithNavController(controller)
    }

    private fun openCloseNavigationDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            drawer.openDrawer(GravityCompat.START)
        }
    }

    override fun navigateToSignIn() {
        findNavController().navigate(R.id.action_authorizedFragment_to_signInFragment)
    }
}
