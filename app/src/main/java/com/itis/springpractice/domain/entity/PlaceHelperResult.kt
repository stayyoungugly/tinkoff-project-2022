package com.itis.springpractice.domain.entity

sealed class PlaceHelperResult

data class PlaceHelperSuccess(
    val placeId: String,
    val status: String,
) : PlaceHelperResult()

data class PlaceHelperError(val message: String, val status: String) : PlaceHelperResult()
