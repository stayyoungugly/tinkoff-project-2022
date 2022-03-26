package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.VerificationResponse
import com.itis.springpractice.domain.entity.VerificationEntity
import retrofit2.Response

class VerificationMapper {
    fun mapToVerificationEntity(response: Response<VerificationResponse>): VerificationEntity {
        return if (response.isSuccessful) {
            VerificationEntity(
                email = response.body()?.users?.get(0)?.email,
                localId = response.body()?.users?.get(0)?.localId,
                displayName = response.body()?.users?.get(0)?.displayName,
                passwordHash = response.body()?.users?.get(0)?.passwordHash,
                emailVerified = response.body()?.users?.get(0)?.emailVerified,
                providerId = response.body()?.users?.get(0)?.providerUserInfo?.get(0)?.providerId,
                federatedId = response.body()?.users?.get(0)?.providerUserInfo?.get(0)?.federatedId,
            )
        } else {
            val errorResponse: ErrorResponse =
                Gson().fromJson(response.errorBody()?.string(), ErrorResponse::class.java)
            return VerificationEntity(
                errorMessage = errorResponse.error.message
            )
        }
    }
}
