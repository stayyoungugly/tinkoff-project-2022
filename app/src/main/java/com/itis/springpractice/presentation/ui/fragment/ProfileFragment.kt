package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentProfileBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.ProfileViewModel
import timber.log.Timber


class ProfileFragment : Fragment(), OnMapReadyCallback {
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
            findNavController().navigate(R.id.action_profileFragment_to_signInFragment)
        }
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.style_json
                )
            )
            if (!success) {
                Timber.e("Style parsing failed.")
            }
        } catch (e: NotFoundException) {
            Timber.e("Can't find style. Error: %s", e.message)
        }
        val positionKazan = LatLng(55.78, 49.12)
        val cameraPosition = CameraPosition.Builder()
            .target(positionKazan)
            .zoom(15f)
            //.bearing(90f)
            .tilt(10f)
            .build()
        googleMap.addMarker(
            MarkerOptions()
                .position(positionKazan)
                .title("Kazan")
        )
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
}
