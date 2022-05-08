package com.itis.springpractice.presentation.ui.fragment

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentMapBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.MapViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.StyleType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.search.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider


class MapFragment : Fragment(), UserLocationObjectListener, CameraListener, Session.SearchListener {
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapViewModel: MapViewModel
    private lateinit var mapView: MapView
    private lateinit var userLocationLayer: UserLocationLayer
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mapCity: Map
    private lateinit var searchSession: Session
    private lateinit var glide: RequestManager

    private var defaultLocation = Point(0.0, 0.0)
    private var routeStartLocation: Point? = defaultLocation

    private var permissionLocation = false
    private var followUserLocation = false

    private val mapKit by lazy {
        MapKitFactory.getInstance()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(context)
    }

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
        bottomInit()
        glide = Glide.with(this)
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        initObjects()
        binding.btnSignOut.setOnClickListener {
            mapViewModel.onDeleteTokenClick()
            findNavController().navigate(R.id.action_mapFragment_to_signInFragment)
        }
        mapView = binding.mapCity
        mapInit()
        checkPermission()
        userInterface()
    }

    private fun checkPermission() {
        val permissionLocation =
            context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            getLocationPermissions()
        } else {
            userLocationConfig()
        }
    }

    private fun mapInit() {
        mapCity = mapView.map
        mapCity.mapType = MapType.VECTOR_MAP
        mapCity.setMapStyle(StyleType.V_MAP2.toString())
        mapCity.addTapListener(geoObjectTapListener)
        mapCity.isZoomGesturesEnabled = true
        mapCity.isRotateGesturesEnabled = true
        mapCity.isFastTapEnabled = true
        mapCity.isIndoorEnabled = true
    }

    private fun userLocationConfig() {
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)
        mapCity.addCameraListener(this)
        cameraUserPosition()
        permissionLocation = true
    }

    private fun bottomInit() {
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomLayout.root)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        })
    }

    private fun bottomModify(params: BusinessObjectMetadata) {
        val options = RequestOptions()
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
        val bottomBinding = binding.bottomLayout
        with(bottomBinding) {
            val bottomSheetBehavior = BottomSheetBehavior.from(root)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            tvName.text = params.name
            tvDescription.text = params.address.formattedAddress
            val url = params.advertisement?.images?.get(0)?.url
//            glide.load(url)
//                .apply(options)
//                .into(ivPicture)
        }
    }

    private fun modifyMap(location: Point?) {
        if (location != null) {
            mapCity.move(
                CameraPosition(Point(location.latitude, location.longitude), 18.0f, 0.0f, 0.0f),
                Animation(
                    Animation.Type.SMOOTH, 1F
                ),
                null
            )
        }
    }

    private fun getLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun userInterface() {
        val mapLogoAlignment = Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM)
        mapCity.logo.setAlignment(mapLogoAlignment)
        binding.userLocationFab.setOnClickListener {
            if (permissionLocation) {
                cameraUserPosition()
                followUserLocation = true
            } else {
                checkPermission()
            }
        }
    }

    private fun cameraUserPosition() {
        if (userLocationLayer.cameraPosition() != null) {
            routeStartLocation = userLocationLayer.cameraPosition()?.target
            modifyMap(routeStartLocation)
        } else {
            modifyMap(defaultLocation)
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                userLocationConfig()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                userLocationConfig()
            }
            else -> {
                showMessage("Скоро будет доступен поиск мест")
            }
        }
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        setAnchor()
        userLocationView.pin.setIcon(ImageProvider.fromResource(context, R.drawable.arrow))
        userLocationView.arrow.setIcon(ImageProvider.fromResource(context, R.drawable.arrow))
        userLocationView.accuracyCircle.fillColor = Color.LTGRAY and -0x66000001
    }

    override fun onObjectRemoved(userView: UserLocationView) {}

    override fun onObjectUpdated(userView: UserLocationView, objEvent: ObjectEvent) {}

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finish: Boolean
    ) {
        if (finish) {
            if (followUserLocation) {
                setAnchor()
            }
        } else {
            if (!followUserLocation) {
                noAnchor()
            }
        }
    }

    private fun setAnchor() {
        userLocationLayer.setAnchor(
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.5).toFloat()),
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.83).toFloat())
        )
        binding.userLocationFab.setImageResource(R.drawable.ic_my_location_black)
        followUserLocation = false
    }

    private fun noAnchor() {
        userLocationLayer.resetAnchor()
        binding.userLocationFab.setImageResource(R.drawable.ic_location_searching_black)
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
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

    private val geoObjectTapListener =
        GeoObjectTapListener { event ->
            val selectionMetadata = event.geoObject
                .metadataContainer
                .getItem(GeoObjectSelectionMetadata::class.java)
            val point = event.geoObject.geometry[0].point
            val searchManager = SearchFactory.getInstance().createSearchManager(
                SearchManagerType.ONLINE
            )
            val options = SearchOptions()
            options.searchTypes = 2
            searchSession = point?.let {
                searchManager.submit(it, 16, options, this)
            }!!
            mapCity.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
            true
        }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onSearchResponse(response: Response) {
        val params = response.collection.children.firstOrNull()?.obj
            ?.metadataContainer
            ?.getItem(BusinessObjectMetadata::class.java)
        if (params != null) {
            bottomModify(params)
        }
    }

    override fun onSearchError(error: Error) {
        showMessage(error.toString())
    }
}

