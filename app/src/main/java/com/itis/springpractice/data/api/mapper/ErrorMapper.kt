package com.itis.springpractice.data.api.mapper

import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.domain.entity.ErrorEntity
import retrofit2.Response

class ErrorMapper {
    fun mapToErrorEntity(errorResponse: Response<ErrorResponse>): ErrorEntity {
        return if (!errorResponse.isSuccessful) {
            val body = requireNotNull(errorResponse.body())
            ErrorEntity(
                code = body.error.code,
                message = body.error.message
            )
        } else ErrorEntity(
            code = 200,
            message = "OK"
        )
    }
}
