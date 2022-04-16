package com.itis.springpractice.data.api.firebase

import com.itis.springpractice.data.response.PlaceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("json")
    suspend fun getPlaceId(
        @Query("latlng") city: String
    ): Response<PlaceResponse>
}
