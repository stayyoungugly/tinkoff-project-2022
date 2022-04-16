package com.itis.springpractice.domain.usecase.geocoding

import com.itis.springpractice.domain.entity.PlaceHelperResult
import com.itis.springpractice.domain.repository.PlaceHelperRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetPlaceIdUseCase(
    private val placeHelperRepository: PlaceHelperRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) {
    suspend operator fun invoke(
        lat: Double,
        lng: Double
    ): PlaceHelperResult {
        return withContext(dispatcher) {
            placeHelperRepository.getPlaceId(lat, lng)
        }
    }
}
