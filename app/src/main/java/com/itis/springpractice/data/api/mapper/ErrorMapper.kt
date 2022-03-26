package com.itis.springpractice.data.api.mapper

import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.domain.entity.ErrorEntity
import retrofit2.Response

class ErrorMapper {
    fun mapToErrorEntity(errorResponse: Response<ErrorResponse>): ErrorEntity {
        return if (!errorResponse.isSuccessful) {
            ErrorEntity(
                code = errorResponse.body()?.error?.code,
                message = errorResponse.body()?.error?.message
            )
        } else ErrorEntity(
            code = 400,
            message = "OK"
        )
    }
}
