package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.usecase.review.AddReviewOnPlaceUseCase
import com.itis.springpractice.domain.usecase.review.GetReviewsByPlaceUseCase
import kotlinx.coroutines.launch

class PlaceReviewViewModel(
    private val getReviewsByPlaceUseCase: GetReviewsByPlaceUseCase,
    private val addReviewOnPlaceUseCase: AddReviewOnPlaceUseCase
) : ViewModel() {

    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

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
}
