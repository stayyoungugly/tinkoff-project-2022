package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.Place
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.usecase.review.DeleteReviewUseCase
import com.itis.springpractice.domain.usecase.user.GetUserNicknameUseCase
import com.itis.springpractice.domain.usecase.user.GetUserReviewsUseCase
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.runtime.Error
import kotlinx.coroutines.launch

class UserReviewsViewModel(
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val getUserReviewsUseCase: GetUserReviewsUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase
) : ViewModel(), Session.SearchListener {
    private lateinit var searchSession: Session
    private lateinit var uri: String
    private var list = ArrayList<Review>()

    private lateinit var reviewItem: Review

    private val searchManager by lazy {
        SearchFactory.getInstance().createSearchManager(
            SearchManagerType.COMBINED
        )
    }

    private var _reviews: SingleLiveEvent<ArrayList<Review>> = SingleLiveEvent()
    val reviews: LiveData<ArrayList<Review>> = _reviews

    private var _error: SingleLiveEvent<Throwable> = SingleLiveEvent()
    val error: LiveData<Throwable> = _error

    fun onGetReviews(nickname: String?) {
        list.clear()
        var listItem: ArrayList<Review>
        _reviews.value = ArrayList()
        viewModelScope.launch {
            try {
                listItem = if (nickname != null && nickname != getUserNicknameUseCase()) {
                    getUserReviewsUseCase(nickname) as ArrayList<Review>
                } else getUserReviewsUseCase(getUserNicknameUseCase()) as ArrayList<Review>
                listItem
                    .forEach { review ->
                        reviewItem = review
                        searchGeoObjectInfo(generateUri(review.uri))
                    }
            } catch (ex: Exception) {
                println(ex.message)
                _error.value = ex
            }
        }
    }

    private fun generateUri(uri: String): String {
        return "ymapsbm1://org?oid=$uri"
    }

    fun deleteReview(uri: String) {
        viewModelScope.launch {
            try {
                deleteReviewUseCase(getUserNicknameUseCase(), uri)
                for (review in list) {
                    if (review.uri == uri) {
                        list.remove(review)
                        _reviews.value = list
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
                reviewItem.place = generatePlaceModel(uri, paramsInfo, point)
                reviewItem.let {
                    list.add(it)
                    _reviews.value = list
                }
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

    private val _nickname: SingleLiveEvent<String> = SingleLiveEvent()
    val nickname: LiveData<String> = _nickname

    fun getUserNickname() {
        viewModelScope.launch {
            try {
                _nickname.value = getUserNicknameUseCase()
            } catch (ex: Exception) {
                _error.value = ex
            }
        }
    }
}
