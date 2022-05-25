package com.itis.springpractice.presentation.viewmodel

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.MyApplication
import com.itis.springpractice.domain.entity.ReviewEntity
import com.itis.springpractice.domain.usecase.review.AddReviewOnPlaceUseCase
import com.itis.springpractice.domain.usecase.review.GetReviewsByPlaceUseCase
import com.itis.springpractice.domain.usecase.token.DeleteTokenUseCase
import kotlinx.coroutines.launch

class MapViewModel(
    private val deleteTokenUseCase: DeleteTokenUseCase,
    private val getReviewsByPlaceUseCase: GetReviewsByPlaceUseCase,
    private val addReviewOnPlaceUseCase: AddReviewOnPlaceUseCase
) : ViewModel() {

    fun onDeleteTokenClick() {
        viewModelScope.launch {
            deleteTokenUseCase()
        }
    }

    fun isPermissionsAllowed(): Boolean {
        var flag = false
        viewModelScope.launch {
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
        }
        return flag
    }

    private var _reviewAdded: MutableLiveData<Boolean> = MutableLiveData()
    val reviewAdded: LiveData<Boolean> = _reviewAdded

    fun addReviewOnPlace(placeURI: String, textReview: String, rating: Int) {
        viewModelScope.launch {
            try {
                _reviewAdded.value = addReviewOnPlaceUseCase(placeURI, textReview, rating)
            } catch (ex: Exception) {
                _reviewAdded.value = false
            }
        }
    }

    private var _reviewList: MutableLiveData<Result<List<ReviewEntity?>>> = MutableLiveData()
    val reviewList: LiveData<Result<List<ReviewEntity?>>> = _reviewList

    fun getReviewsByPlace(placeURI: String) {
        viewModelScope.launch {
            try {
                _reviewList.value = Result.success(getReviewsByPlaceUseCase(placeURI))
            } catch (ex: Exception) {
                _reviewList.value = Result.failure(ex)
            }
        }
    }
}
