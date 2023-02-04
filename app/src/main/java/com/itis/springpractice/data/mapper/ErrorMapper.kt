package com.itis.springpractice.data.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.domain.entity.ErrorModel
import retrofit2.Response

class ErrorMapper {
    fun mapToErrorEntity(response: Response<ErrorResponse>): ErrorModel {
        return if (response.isSuccessful) {
            ErrorModel(
                code = 200,
                message = "OK"
            )
        } else {
            val body = requireNotNull(response.errorBody())
            val errorResponse: ErrorResponse =
                Gson().fromJson(body.string(), ErrorResponse::class.java)
            ErrorModel(
                code = errorResponse.error.code,
                message = errorResponse.error.message
            )
        }
    }
}
