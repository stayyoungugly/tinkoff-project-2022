package com.itis.springpractice.presentation.ui.fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.BuildConfig
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentMapBinding
import com.itis.springpractice.di.PlaceContainer
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.PlaceHelperSuccess
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.MapViewModel
import timber.log.Timber

class MapFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapViewModel: MapViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var placesClient: PlacesClient

    private var placeId: String? = ""

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
        initObservers()
        initPlaces()

        binding.btnSignOut.setOnClickListener {
            mapViewModel.onDeleteTokenClick()
            findNavController().navigate(R.id.action_mapFragment_to_signInFragment)
        }
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val fields = listOf(Place.Field.ID, Place.Field.NAME)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
            .build(requireContext())
        resultLauncher.launch(intent)
    }


    // код пока просто как шаблон, надо еще доделать
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            with(result) {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.let {
                            val place =
                                data?.let { intent -> Autocomplete.getPlaceFromIntent(intent) }
                            if (place != null) {
                                Timber.e("Place: ${place.name}, ${place.id}")
                            }
                        }
                    }
                    AutocompleteActivity.RESULT_ERROR -> {
                        // TODO: Handle the error.
                        data?.let {
                            val status = data?.let { it1 -> Autocomplete.getStatusFromIntent(it1) }
                            if (status != null) {
                                Timber.e(status.statusMessage ?: "")
                            }
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        // The user canceled the operation.
                    }
                }
            }
        }

    private fun initPlaces() {
        Places.initialize(requireContext(), BuildConfig.SECRET_KEY)
        placesClient = Places.createClient(requireContext())
    }

    private fun initObjects() {
        val factory = AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences),
            PlaceContainer
        )
        mapViewModel = ViewModelProvider(
            this,
            factory
        ).get(MapViewModel::class.java)
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

        googleMap.setOnMapClickListener {
            mapViewModel.onGetPlaceIdClick(it.latitude, it.longitude)
            if (!placeId.isNullOrEmpty()) {
                getPlaceInfo()
            }
        }
    }

    private fun getPlaceInfo() {
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME)
        val request = placeId?.let { FetchPlaceRequest.newInstance(it, placeFields) }
        if (request != null) {
            placesClient.fetchPlace(request)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    val place = response.place
                    Timber.e("Place found: ${place.name}")
                }.addOnFailureListener { exception: Exception ->
                    if (exception is ApiException) {
                        Timber.e("Place not found: ${exception.message}")
                        val statusCode = exception.statusCode
                        exception.message?.let { showMessage(it) }
                    }
                }
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun initObservers() {
        mapViewModel.placeHelperResult.observe(viewLifecycleOwner) {
            placeId = if (it is PlaceHelperSuccess) {
                it.placeId
            } else {
                ""
            }
        }
    }
}
