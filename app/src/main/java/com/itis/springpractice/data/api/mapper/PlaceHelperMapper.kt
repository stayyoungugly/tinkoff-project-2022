package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.PlaceErrorResponse
import com.itis.springpractice.data.response.PlaceResponse
import com.itis.springpractice.domain.entity.PlaceHelperError
import com.itis.springpractice.domain.entity.PlaceHelperResult
import com.itis.springpractice.domain.entity.PlaceHelperSuccess
import retrofit2.Response

class PlaceHelperMapper {
    fun mapToPlaceHelper(response: Response<PlaceResponse>): PlaceHelperResult {
        return if (response.isSuccessful) {
            val body = requireNotNull(response.body())
            PlaceHelperSuccess(
                status = body.status,
                placeId = body.results[0].placeId
            )
        } else {
            val body = requireNotNull(response.errorBody())
            val placeErrorResponse: PlaceErrorResponse =
                Gson().fromJson(body.string(), PlaceErrorResponse::class.java)
            return PlaceHelperError(placeErrorResponse.errorMessage, placeErrorResponse.status)
        }
    }
}
