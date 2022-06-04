package com.itis.springpractice.presentation.ui.fragment

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.DialogSearchPlaceBinding
import com.itis.springpractice.databinding.FragmentMapBinding
import com.itis.springpractice.di.FriendContainer
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.ui.fragment.extension.findParent
import com.itis.springpractice.presentation.viewmodel.MapViewModel
import com.itis.springpractice.presentation.viewmodel.PlaceInfoViewModel
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
import com.yandex.mapkit.uri.UriObjectMetadata
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.Error
import com.yandex.runtime.i18n.I18nManagerFactory
import com.yandex.runtime.image.ImageProvider
import timber.log.Timber


class MapFragment : Fragment(R.layout.fragment_map), UserLocationObjectListener, CameraListener,
    GeoObjectTapListener, InputListener, Session.SearchListener, MapObjectTapListener {
    private lateinit var userLocationLayer: UserLocationLayer
    private val binding by viewBinding(FragmentMapBinding::bind)
    private lateinit var dialogBinding: DialogSearchPlaceBinding
    private lateinit var uri: String
    private lateinit var suggestResultView: ListView

    private lateinit var searchSession: Session

    private val searchManager by lazy {
        SearchFactory.getInstance().createSearchManager(
            SearchManagerType.COMBINED
        )
    }

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
    private var suggestResult = ArrayList<String>()

    private lateinit var resultAdapter: ArrayAdapter<String>

    companion object {
        const val defaultZoomValue = 19.0f
        const val mediumZoomValue = 18.0f
        const val smallZoomValue = 16.0f
        const val wideZoomValue = 14.0f
        const val zeroFloatValue = 0.0f
        const val durationValue = 1f
        const val widthRatio = 0.5
        const val heightRatio = 0.83
        const val tiltFloatValue = 45.0f
        const val pointOffset = 0.00015
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
            UserContainer(sharedPreferences),
            FriendContainer(sharedPreferences)
        )
    }

    private val placeInfoViewModel by viewModels<PlaceInfoViewModel> {
        AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences),
            UserContainer(sharedPreferences),
            FriendContainer(sharedPreferences)
        )
    }

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapCity.addInputListener(this)
        mapCity.addTapListener(this)
        initObservers()
        binding.btnSignOut.setOnClickListener {
            onSignOutClick()
        }
        binding.searchFab.setOnClickListener {
            navigateToSearchPlace()
        }
        checkPermission()
        userInterface()

    }

    private fun dialogSuggestConfig() {
        resultAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            suggestResult
        )

        suggestResultView = dialogBinding.lvPlaces
        suggestResultView.adapter = resultAdapter

        dialogBinding.etPlace.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                mapViewModel.requestSuggest(
                    editable.toString(),
                    mapCity.visibleRegion
                )
            }
        })

        suggestResultView.visibility = View.INVISIBLE
    }

    private fun navigateToSearchPlace() {
        DialogSearchPlaceBinding.inflate(layoutInflater).let {
            dialogBinding = it
        }

        dialogSuggestConfig()
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.search_places))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.find), null)
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            onSearchPlace(dialogBinding.etPlace.toString())
            dialog.dismiss()
        }
        suggestResultView.setOnItemClickListener { _, _, position, _ ->
            val itemValue = suggestResultView.getItemAtPosition(position) as String
            onSearchPlace(itemValue)
            dialog.dismiss()
        }
    }

    private fun onSearchPlace(itemValue: String) {
        searchSession = searchManager.submit(
            itemValue,
            VisibleRegionUtils.toPolygon(mapView.map.visibleRegion),
            SearchOptions(),
            this
        )
    }

    private fun onSignOutClick() {
        mapViewModel.onDeleteClick()
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

    private fun modifyMap(location: Point?, isAnimated: Boolean, zoomValue: Float) {
        if (location != null) {
            val cameraPosition = CameraPosition(
                Point(location.latitude, location.longitude),
                zoomValue,
                zeroFloatValue,
                tiltFloatValue
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
            modifyMap(routeStartLocation, isAnimated, defaultZoomValue)
        } else {
            modifyMap(defaultLocation, isAnimated, defaultZoomValue)
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
            placeInfoViewModel.searchGeoObjectInfo(uri)
        }
        return true
    }

    private fun initObservers() {
        mapViewModel.error.observe(viewLifecycleOwner) {
            Timber.e(it)
            showMessage(getString(R.string.try_again_error))
        }

        mapViewModel.suggestResult.observe(viewLifecycleOwner) {
            suggestResult.clear()
            suggestResult.addAll(it)
            resultAdapter.notifyDataSetChanged()
            println(resultAdapter.count)
            suggestResultView.visibility = View.VISIBLE
        }

        placeInfoViewModel.place.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                if (it.address.isNotEmpty()) {
                    bottomModify()
                }
            }, onFailure = {
                showMessage(getString(R.string.not_found))
            })
        }
    }

    override fun onMapTap(map: Map, point: Point) {
        mapCity.deselectGeoObject()
    }

    override fun onMapLongTap(map: Map, point: Point) {}

    override fun onSearchResponse(response: Response) {
        val mapObjects = mapView.map.mapObjects
        mapObjects.clear()
        mapObjects.addTapListener(this)
        for (searchResult in response.collection.children) {
            val point = searchResult.obj?.geometry?.get(0)?.point
            val resultLocation = point?.let { Point(it.latitude + pointOffset, it.longitude) }
            if (resultLocation != null) {
                mapObjects.addPlacemark(
                    resultLocation,
                    ImageProvider.fromResource(requireContext(), R.drawable.search_result)
                )
            }
        }
        val count = response.collection.children.size
        if (count == 0) {
            showMessage(getString(R.string.place_no_found))
        } else {
            val first = response.collection.children[0].obj?.geometry?.get(0)?.point

            val uriLink =
                response.collection.children[0].obj?.metadataContainer?.getItem(UriObjectMetadata::class.java)?.uris?.first()?.value
            if (!uriLink.isNullOrEmpty()) {
                uri = uriLink
                placeInfoViewModel.searchGeoObjectInfo(uri)
            }

            var zoom = defaultZoomValue
            when {
                count >= 10 -> {
                    zoom = wideZoomValue
                }
                count >= 5 -> {
                    zoom = smallZoomValue
                }
                count > 1 -> {
                    zoom = mediumZoomValue
                }
            }
            modifyMap(first, true, zoom)
            showMessage("Найдено мест: $count")
        }
    }

    override fun onSearchError(error: Error) {
        showMessage(getString(R.string.fail_place_found))
    }

    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        modifyMap(point, true, defaultZoomValue)
        return true
    }

}

