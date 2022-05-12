package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.content.SharedPreferences
import android.graphics.PointF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.yandex.mapkit.layers.GeoObjectTapEvent
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


class MapFragment : Fragment(), UserLocationObjectListener, CameraListener, Session.SearchListener,
    GeoObjectTapListener {
    private lateinit var binding: FragmentMapBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var searchSession: Session

    private val glideOptions by lazy {
        RequestOptions()
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    private val userLocationLayer: UserLocationLayer by lazy {
        mapKit.createUserLocationLayer(mapView.mapWindow).apply {
            isVisible = true
            isHeadingEnabled = true
        }
    }

    private val mapViewModel by viewModels<MapViewModel> {
        AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences)
        )
    }

    private val mapView: MapView by lazy {
        binding.mapCity
    }

    private val mapCity: Map by lazy {
        mapView.map.apply {
            mapType = MapType.VECTOR_MAP
            setMapStyle(StyleType.V_MAP2.toString())
            isZoomGesturesEnabled = true
            isRotateGesturesEnabled = true
            isFastTapEnabled = true
            isIndoorEnabled = true
        }
    }

    private val glide: RequestManager by lazy {
        Glide.with(this)
    }

    private val mapKit by lazy {
        MapKitFactory.getInstance()
    }

    private var defaultLocation = Point(0.0, 0.0)
    private var routeStartLocation: Point? = defaultLocation

    private var permissionLocation = false
    private var followUserLocation = false

    companion object {
        const val zoomValue = 18.0f
        const val zeroFloatValue = 0.0f
        const val durationValue = 1f
        const val widthRatio = 0.5
        const val heightRatio = 0.83
        const val searchTypesAmount = 2
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
        sharedPreferences = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        binding.btnSignOut.setOnClickListener {
            onSignOutClick()
        }

        checkPermission()
        userInterface()
    }

    private fun onSignOutClick() {
        mapViewModel.onDeleteTokenClick()
        findNavController().navigate(R.id.action_mapFragment_to_signInFragment)
    }

    private fun checkPermission() {
        if (mapViewModel.checkLocationPermission(this)) {
            userLocationConfig()
        } else showMessage("Скоро будет доступен поиск мест")
    }

    private fun userLocationConfig() {
        mapCity.addCameraListener(this)
        cameraUserPosition()
        permissionLocation = true
        userLocationLayer.setObjectListener(this)
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
        val bottomBinding = binding.bottomLayout
        with(bottomBinding) {
            val bottomSheetBehavior = BottomSheetBehavior.from(root)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            tvName.text = params.name
            tvDescription.text = params.address.formattedAddress
            val url = params.advertisement?.images?.get(0)?.url
//            glide.load(url)
//                .apply(glideOptions)
//                .into(ivPicture)
//            ну я потом этот код буду использовать, ладно уж
        }
    }

    private fun modifyMap(location: Point?) {
        if (location != null) {
            mapCity.move(
                CameraPosition(
                    Point(location.latitude, location.longitude),
                    zoomValue,
                    zeroFloatValue,
                    zeroFloatValue
                ),
                Animation(
                    Animation.Type.SMOOTH, durationValue
                ),
                null
            )
        }
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

    override fun onObjectAdded(userLocationView: UserLocationView) {
        setAnchor()
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

    private fun searchGeoObjectInfo(event: GeoObjectTapEvent) {
        val point = event.geoObject.geometry[0].point
        val searchManager = SearchFactory.getInstance().createSearchManager(
            SearchManagerType.ONLINE
        )
        val options = SearchOptions()
        options.searchTypes = searchTypesAmount
        startSession(point, searchManager, options)
    }

    private fun startSession(
        point: Point?,
        searchManager: SearchManager,
        options: SearchOptions
    ) {
        searchSession = point?.let {
            searchManager.submit(it, 16, options, this)
        }!!
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

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onObjectTap(event: GeoObjectTapEvent): Boolean {
        mapCity.addTapListener(this)
        selectionGeoObject(event)
        searchGeoObjectInfo(event)
        return true
    }
}

