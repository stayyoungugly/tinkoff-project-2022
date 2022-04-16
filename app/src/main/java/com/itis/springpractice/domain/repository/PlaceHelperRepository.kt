package com.itis.springpractice.domain.repository

import com.itis.springpractice.domain.entity.PlaceHelperResult

interface PlaceHelperRepository {
    suspend fun getPlaceId(lat: Double, lng: Double): PlaceHelperResult
}
