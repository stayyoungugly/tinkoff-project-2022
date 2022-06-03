package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.Place
import com.itis.springpractice.domain.usecase.user.AddUserLikeUseCase
import com.itis.springpractice.domain.usecase.user.DeleteUserLikeUseCase
import com.itis.springpractice.domain.usecase.user.GetUserNicknameUseCase
import com.itis.springpractice.domain.usecase.user.IsPlaceLikedUseCase
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import kotlinx.coroutines.launch

class PlaceInfoViewModel(
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val addUserLikeUseCase: AddUserLikeUseCase,
    private val deleteUserLikeUseCase: DeleteUserLikeUseCase,
    private val isPlaceLikedUseCase: IsPlaceLikedUseCase
) : ViewModel(), Session.SearchListener {
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
        )

    }

    private val _isPlaceLiked: MutableLiveData<String> = MutableLiveData()
    var isPlaceLiked: LiveData<String> = _isPlaceLiked

    fun isPlaceLiked(uri: String) {
        try {
            viewModelScope.launch {
                _isPlaceLiked.value = isPlaceLikedUseCase(getUserNicknameUseCase(), uri)
            }
        } catch (ex: Exception) {
            _error.value = ex
            println(ex.message)
        }
    }

    override fun onSearchError(error: Error) {
        _error.value = Throwable(error.toString())
    }

    fun deleteLike(uriPlace: String) {
        viewModelScope.launch {
            try {
                deleteUserLikeUseCase(getUserNicknameUseCase(), uriPlace)
            } catch (ex: Exception) {
                _error.value = Throwable(error.toString())
            }
        }
    }

    fun addLike(uriPlace: String) {
        viewModelScope.launch {
            try {
                addUserLikeUseCase(getUserNicknameUseCase(), uriPlace)
            } catch (ex: Exception) {
                _error.value = Throwable(error.toString())
                println(ex.message)
            }
        }
    }
}
