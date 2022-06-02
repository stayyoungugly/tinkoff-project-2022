package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.itis.springpractice.domain.entity.Place
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error

class PlaceInfoViewModel : ViewModel(), Session.SearchListener {
    private lateinit var searchSession: Session
    private lateinit var uri: String

    private val searchManager by lazy {
        SearchFactory.getInstance().createSearchManager(
            SearchManagerType.COMBINED
        )
    }

    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

    private val _place: MutableLiveData<Result<Place>> = MutableLiveData()
    val place: LiveData<Result<Place>> = _place

    fun searchGeoObjectInfo(uriPlace: String) {
        uri = uriPlace
        val options = SearchOptions()
        options.snippets = Snippet.PHOTOS.value
        options.snippets = Snippet.BUSINESS_IMAGES.value
        options.snippets = Snippet.ENCYCLOPEDIA.value

        try {
            startSession(options, uri)
        } catch (ex: Exception) {
            _error.value = ex
        }
    }

    private fun startSession(
        options: SearchOptions,
        uri: String
    ) {
        searchSession = searchManager.resolveURI(uri, options, this)
    }

    override fun onSearchResponse(response: Response) {
        try {
            val paramsInfo = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(BusinessObjectMetadata::class.java)
            if (paramsInfo != null) {
                _place.value = Result.success(
                    generatePlaceModel(uri, paramsInfo)
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
            category = params.categories[0].name,
            phones = params.phones[0].formattedNumber,
            address = params.address.formattedAddress,
            photoUrl = params.advertisement?.images?.get(1)?.url,
            description = params.advertisement?.about
        )
    }

    override fun onSearchError(error: Error) {
        _error.value = Throwable(error.toString())
    }
}
