package com.itis.springpractice.presentation.ui.fragment

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentMapBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.ui.fragment.extension.findParent
import com.itis.springpractice.presentation.viewmodel.MapViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.StyleType
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.uri.UriObjectMetadata
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.i18n.I18nManagerFactory
import com.yandex.runtime.image.ImageProvider
import timber.log.Timber


class MapFragment : Fragment(R.layout.fragment_map), UserLocationObjectListener, CameraListener,
    GeoObjectTapListener {
    private lateinit var userLocationLayer: UserLocationLayer
    private val binding by viewBinding(FragmentMapBinding::bind)
    private lateinit var uri: String

    private val bottomSheetDialogFragment: BottomSheetDialogFragment
        get() = BottomSheetFragment(uri)

    private val mapView: MapView
        get() = binding.mapCity

    private val mapCity: Map
        get() = mapView.map.apply {
            mapType = MapType.VECTOR_MAP
            setMapStyle(StyleType.V_MAP2.toString())
            isZoomGesturesEnabled = true
            isRotateGesturesEnabled = true
            isFastTapEnabled = true
            isIndoorEnabled = true
        }

    private val mapKit
        get() = MapKitFactory.getInstance()

    private var defaultLocation = Point(0.0, 0.0)
    private var routeStartLocation: Point? = defaultLocation

    private var permissionLocation = false
    private var followUserLocation = false

    companion object {
        const val zoomValue = 19.0f
        const val zeroFloatValue = 0.0f
        const val durationValue = 1f
        const val widthRatio = 0.5
        const val heightRatio = 0.83
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
        I18nManagerFactory.setLocale("ru_RU")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(context)
    }

    private val mapViewModel by viewModels<MapViewModel> {
        AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences),
            UserContainer(sharedPreferences)
        )
    }

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapCity.addTapListener(this)
        binding.btnSignOut.setOnClickListener {
            onSignOutClick()
        }
        initObservers()
        checkPermission()
        userInterface()

    }

    private fun onSignOutClick() {
        mapViewModel.onDeleteTokenClick()
        (this.findParent<AuthorizedFragment>() as? Callbacks)?.navigateToSignIn()
    }

    private fun createUserLocationLayer() {
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow).apply {
            isVisible = true
            isHeadingEnabled = true
        }
    }

    private fun checkPermission() {
        if (mapViewModel.isPermissionsAllowed()) {
            userLocationConfig()
        } else getLocationPermissions()
    }

    private fun getLocationPermissions() {
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
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
                showMessage(getString(R.string.search_will_be_available))
            }
        }
    }

    private fun userLocationConfig() {
        mapCity.addCameraListener(this)
        createUserLocationLayer()
        cameraUserPosition(false)
        permissionLocation = true
        userLocationLayer.setObjectListener(this)
    }

    private fun bottomModify() {
        bottomSheetDialogFragment.show(parentFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun modifyMap(location: Point?, isAnimated: Boolean) {
        if (location != null) {
            val cameraPosition = CameraPosition(
                Point(location.latitude, location.longitude),
                zoomValue,
                zeroFloatValue,
                zeroFloatValue
            )
            if (isAnimated) {
                mapCity.move(
                    cameraPosition,
                    Animation(
                        Animation.Type.SMOOTH, durationValue
                    ),
                    null
                )
            } else {
                mapCity.move(
                    cameraPosition
                )
            }
        }
    }

    private fun userInterface() {
        val mapLogoAlignment = Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM)
        mapCity.logo.setAlignment(mapLogoAlignment)
        binding.userLocationFab.setOnClickListener {
            if (permissionLocation) {
                cameraUserPosition(true)
                followUserLocation = true
            } else {
                checkPermission()
            }
        }
    }

    private fun cameraUserPosition(isAnimated: Boolean) {
        if (userLocationLayer.cameraPosition() != null) {
            routeStartLocation = userLocationLayer.cameraPosition()?.target
            modifyMap(routeStartLocation, isAnimated)
        } else {
            modifyMap(defaultLocation, isAnimated)
        }
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        setAnchor()
        userLocationView.accuracyCircle.fillColor = Color.TRANSPARENT
        userLocationView.pin.setIcon(ImageProvider.fromResource(context, R.drawable.arrow))
        userLocationView.arrow.setIcon(ImageProvider.fromResource(context, R.drawable.arrow))
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
            PointF((mapView.width * widthRatio).toFloat(), (mapView.height * widthRatio).toFloat()),
            PointF((mapView.width * widthRatio).toFloat(), (mapView.height * heightRatio).toFloat())
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

    private fun selectionGeoObject(event: GeoObjectTapEvent) {
        val selectionMetadata = event.geoObject
            .metadataContainer
            .getItem(GeoObjectSelectionMetadata::class.java)
        mapCity.selectGeoObject(selectionMetadata.id, selectionMetadata.layerId)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onObjectTap(event: GeoObjectTapEvent): Boolean {
        selectionGeoObject(event)
        val uriLink =
            event.geoObject.metadataContainer.getItem(UriObjectMetadata::class.java)?.uris?.first()?.value
        if (!uriLink.isNullOrEmpty()) {
            uri = uriLink
            println(uri)
            bottomModify()
        }
        return true
    }

    private fun initObservers() {
        mapViewModel.error.observe(viewLifecycleOwner) {
            Timber.e(it)
            showMessage(getString(R.string.try_again_error))
        }
    }
}

