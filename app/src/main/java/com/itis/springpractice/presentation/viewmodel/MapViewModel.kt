package com.itis.springpractice.presentation.viewmodel

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.MyApplication
import com.itis.springpractice.domain.entity.Place
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.usecase.review.AddReviewOnPlaceUseCase
import com.itis.springpractice.domain.usecase.review.GetReviewsByPlaceUseCase
import com.itis.springpractice.domain.usecase.token.DeleteTokenUseCase
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.search.*
import com.yandex.mapkit.uri.UriObjectMetadata
import com.yandex.runtime.Error
import kotlinx.coroutines.launch

class MapViewModel(
    private val deleteTokenUseCase: DeleteTokenUseCase,
    private val getReviewsByPlaceUseCase: GetReviewsByPlaceUseCase,
    private val addReviewOnPlaceUseCase: AddReviewOnPlaceUseCase
) : ViewModel(), Session.SearchListener {
    private lateinit var searchSession: Session

    companion object {
        const val scaleValue = 16
        const val searchType = 2
    }

    private val searchManager by lazy {
        SearchFactory.getInstance().createSearchManager(
            SearchManagerType.COMBINED
        )
    }

    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

    fun onDeleteTokenClick() {
        viewModelScope.launch {
            try {
                deleteTokenUseCase()
            } catch (ex: Exception) {
                _error.value = ex
            }
        }
    }

    fun isPermissionsAllowed(): Boolean {
        var flag = false
        viewModelScope.launch {
            try {
                MyApplication.appContext.let {
                    flag = (ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED)
                }
            } catch (ex: Exception) {
                _error.value = ex
            }
        }
        return flag
    }

    private val _reviewAdded: MutableLiveData<Boolean> = MutableLiveData()
    val reviewAdded: LiveData<Boolean> = _reviewAdded

    fun addReviewOnPlace(placeURI: String, textReview: String, rating: Int) {
        viewModelScope.launch {
            try {
                _reviewAdded.value = addReviewOnPlaceUseCase(placeURI, textReview, rating)
            } catch (ex: Exception) {
                _reviewAdded.value = false
                _error.value = ex
            }
        }
    }

    private val _reviewList: MutableLiveData<Result<List<Review?>>> = MutableLiveData()
    val reviewList: LiveData<Result<List<Review?>>> = _reviewList

    fun getReviewsByPlace(placeURI: String) {
        viewModelScope.launch {
            try {
                _reviewList.value = Result.success(getReviewsByPlaceUseCase(placeURI))
            } catch (ex: Exception) {
                _reviewList.value = Result.failure(ex)
                _error.value = ex
            }
        }
    }

    private val _place: MutableLiveData<Result<Place>> = MutableLiveData()
    val place: LiveData<Result<Place>> = _place

    fun searchGeoObjectInfo(event: GeoObjectTapEvent) {
        val options = SearchOptions()
        options.searchTypes = searchType
        try {
            event.geoObject.geometry[0].point?.let { startSession(it, options) }
        } catch (ex: Exception) {
            _error.value = ex
        }
    }

    private fun startSession(
        point: Point,
        options: SearchOptions
    ) {
        searchSession = searchManager.submit(point, scaleValue, options, this)
    }

    override fun onSearchResponse(response: Response) {
        try {
            val uri = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(UriObjectMetadata::class.java)
                ?.uris
                ?.firstOrNull()
                ?.value
            val params = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(BusinessObjectMetadata::class.java)
            if (params != null && uri != null) {
                _place.value = Result.success(
                    generatePlaceModel(uri, params)
                )
            } else _error.value = Throwable("Информация не найдена")
        } catch (ex: Exception) {
            _place.value = Result.failure(ex)
            _error.value = ex
        }
    }

    private fun generatePlaceModel(uri: String, params: BusinessObjectMetadata): Place {
        var closed = true
        if (params.closed?.name == "UNKNOWN") {
            closed = false
        }
        return Place(
            uri = uri,
            name = params.name,
            workingHours = params.workingHours?.text,
            closed = closed,
            address = params.address.formattedAddress,
            photoUrl = params.advertisement?.images?.get(1)?.url,
            description = params.advertisement?.about
        )
    }

    override fun onSearchError(error: Error) {
        _error.value = Throwable(error.toString())
    }
}
