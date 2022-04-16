package com.itis.springpractice.data.impl

import com.itis.springpractice.data.api.firebase.GeocodingApi
import com.itis.springpractice.data.api.mapper.PlaceHelperMapper
import com.itis.springpractice.domain.entity.PlaceHelperResult
import com.itis.springpractice.domain.repository.PlaceHelperRepository

class PlaceHelperRepositoryImpl(
    private var api: GeocodingApi,
    private var placeHelperMapper: PlaceHelperMapper
) : PlaceHelperRepository {

    override suspend fun getPlaceId(lat: Double, lng: Double): PlaceHelperResult {
        return placeHelperMapper.mapToPlaceHelper(api.getPlaceId(coordinateConverter(lat, lng)))
    }

    private fun coordinateConverter(lat: Double, lng: Double): String {
        return "$lat,$lng"
    }
}
