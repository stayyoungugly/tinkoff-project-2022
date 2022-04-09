package com.itis.springpractice.presentation.ui.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentMapBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.MapViewModel
import timber.log.Timber


class MapFragment : Fragment(),
    GoogleMap.OnMyLocationButtonClickListener,
    OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapViewModel: MapViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var location: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        initObjects()
        binding.btnSignOut.setOnClickListener {
            mapViewModel.onDeleteTokenClick()
            findNavController().navigate(R.id.action_mapFragment_to_signInFragment)
        }
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    requireContext(), R.raw.style_json
                )
            )
            if (!success) {
                Timber.e("Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Timber.e("Can't find style. Error: %s", e.message)
        }
        val position = LatLng(55.78, 49.12)
        val cameraPosition = CameraPosition.Builder()
            .target(position)
            .zoom(15f)
            //.bearing(90f)
            .tilt(10f)
            .build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        googleMap.isMyLocationEnabled = true
        googleMap.setOnMyLocationButtonClickListener(this)
    }

    @SuppressLint("MissingPermission")
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted so start GPS updates
                val locationRequest: LocationRequest = LocationRequest.create().apply {
                    interval = 1000
                    fastestInterval = 500
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                }
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        for (locationSuccess in locationResult.locations) {
                            location = locationSuccess
                        }
                    }
                }
                fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                val fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    location = it
                }
            }
            else -> {
                // Only approximate location access granted.
                showMessage("Скоро будет доступен поиск мест")
            }
        }
    }

    private fun initObjects() {
        val factory = AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences)
        )
        mapViewModel = ViewModelProvider(
            this,
            factory
        ).get(MapViewModel::class.java)
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    @SuppressLint("MissingPermission")
    private fun modifyMap(googleMap: GoogleMap) {
        val position = LatLng(location.latitude, location.longitude)
        val cameraPosition = CameraPosition.Builder()
            .target(position)
            .zoom(15f)
            //.bearing(90f)
            .tilt(10f)
            .build()
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    override fun onMyLocationButtonClick(): Boolean {
        modifyMap(googleMap)
        return false
    }
}
