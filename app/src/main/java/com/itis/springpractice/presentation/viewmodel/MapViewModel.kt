package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.PlaceHelperResult
import com.itis.springpractice.domain.usecase.geocoding.GetPlaceIdUseCase
import com.itis.springpractice.domain.usecase.token.DeleteTokenUseCase
import kotlinx.coroutines.launch

class MapViewModel(
    private val deleteTokenUseCase: DeleteTokenUseCase,
    private val getPlaceIdUseCase: GetPlaceIdUseCase
) : ViewModel() {

    fun onDeleteTokenClick() {
        viewModelScope.launch {
            deleteTokenUseCase()
        }
    }

    private var _placeHelperResult: MutableLiveData<PlaceHelperResult> = MutableLiveData()
    val placeHelperResult: LiveData<PlaceHelperResult> = _placeHelperResult

    fun onGetPlaceIdClick(lat: Double, lng: Double) {
        viewModelScope.launch {
            _placeHelperResult.value = getPlaceIdUseCase(lat, lng)
        }
    }

}
