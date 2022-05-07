package com.itis.springpractice.presentation.ui.fragment

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentAuthorizedBinding

class AutorizedFragment : Fragment(R.layout.fragment_authorized) {
    private lateinit var controller: NavController
    private lateinit var binding: FragmentAuthorizedBinding
    private lateinit var drawer: DrawerLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAuthorizedBinding.bind(view)

        controller =
            (childFragmentManager.findFragmentById(R.id.authorized_nav_root) as NavHostFragment)
                .navController

        drawer = binding.authorizedDrawer
        val appBarConfiguration = AppBarConfiguration(controller.graph, drawer)
        binding.authorizedToolbar.setupWithNavController(controller, appBarConfiguration)

        binding.navView.setupWithNavController(controller)
        openCloseNavigationDrawer()

        val toggle = ActionBarDrawerToggle(activity, drawer, binding.authorizedToolbar, R.string.drawer_open, R.string.drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        controller =
            (childFragmentManager.findFragmentById(R.id.authorized_nav_root) as NavHostFragment)
                .navController
        return item.onNavDestinationSelected(controller) || super.onOptionsItemSelected(item)
    }

    private fun openCloseNavigationDrawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            drawer.openDrawer(GravityCompat.START)
        }
    }
}
