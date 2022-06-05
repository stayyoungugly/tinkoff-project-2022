package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.Place
import com.itis.springpractice.domain.usecase.user.DeleteUserLikeUseCase
import com.itis.springpractice.domain.usecase.user.GetFavouritePlacesUseCase
import com.itis.springpractice.domain.usecase.user.GetUserNicknameUseCase
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import kotlinx.coroutines.launch

class PlacesFavouriteViewModel(
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val getFavouritePlacesUseCase: GetFavouritePlacesUseCase,
    private val deleteUserLikeUseCase: DeleteUserLikeUseCase
) : ViewModel(), Session.SearchListener {
    private lateinit var searchSession: Session
    private lateinit var uri: String
    private var list = ArrayList<Place>()

    private val searchManager by lazy {
        SearchFactory.getInstance().createSearchManager(
            SearchManagerType.COMBINED
        )
    }

    private var _favourites: SingleLiveEvent<ArrayList<Place>> = SingleLiveEvent()
    val favourites: LiveData<ArrayList<Place>> = _favourites

    private var _error: SingleLiveEvent<Throwable> = SingleLiveEvent()
    val error: LiveData<Throwable> = _error

    private var _isUser: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val isUser: LiveData<Boolean> = _isUser

    fun onGetFavourites(nickname: String?) {
        list.clear()
        _favourites.value = ArrayList()
        viewModelScope.launch {
            try {
                val listUri = if (nickname != null && nickname != getUserNicknameUseCase()) {
                    _isUser.value = false
                    getFavouritePlacesUseCase(nickname)
                } else {
                    _isUser.value = true
                    getFavouritePlacesUseCase(getUserNicknameUseCase())
                }
                listUri
                    .map { uri -> generateUri(uri) }
                    .forEach { uri -> searchGeoObjectInfo(uri) }
            } catch (ex: Exception) {
                println(ex.message)
                _error.value = ex
            }
        }
    }

    private fun generateUri(uri: String): String {
        return "ymapsbm1://org?oid=$uri"
    }

    fun deleteFromLikes(uri: String) {
        viewModelScope.launch {
            try {
                println(list)
                deleteUserLikeUseCase(getUserNicknameUseCase(), uri)
                for (place in list) {
                    if (place.uri == uri) {
                        list.remove(place)
                        _favourites.value = list
                    }
                }
            } catch (ex: Exception) {
                _error.value = ex
            }
        }
    }

    private fun searchGeoObjectInfo(uriPlace: String) {
        uri = uriPlace
        val options = SearchOptions()
        options.snippets = Snippet.PHOTOS.value
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
            val point = response.collection.children.firstOrNull()?.obj?.geometry?.get(0)?.point
            val paramsInfo = response.collection.children.firstOrNull()?.obj
                ?.metadataContainer
                ?.getItem(BusinessObjectMetadata::class.java)
            if (paramsInfo != null) {
                list.add(generatePlaceModel(uri, paramsInfo, point))
                _favourites.value = list
            } else _error.value = Throwable("Информация не найдена")
        } catch (ex: Exception) {
            _error.value = ex
        }
    }

    override fun onSearchError(error: Error) {
        _error.value = Throwable(error.toString())
    }

    private fun generatePlaceModel(
        uri: String,
        params: BusinessObjectMetadata,
        point: Point?
    ): Place {
        var closed = false
        if (params.closed?.name == "PERMANENT" || params.closed?.name == "TEMPORARY") {
            closed = true
        }
        var hours = "Нет данных"
        if (params.workingHours != null) {
            hours = params.workingHours?.text.toString()
        }
        return Place(
            uri = uri,
            name = params.name,
            workingHours = hours,
            closed = closed,
            category = params.categories[0].name,
            phones = params.phones[0].formattedNumber,
            address = params.address.formattedAddress,
            photoUrl = params.advertisement?.images?.get(1)?.url,
            description = params.advertisement?.about,
            latitude = point?.latitude,
            longitude = point?.longitude
        )

    }
}
