package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.VerificationResponse
import com.itis.springpractice.domain.entity.*
import retrofit2.Response

class VerificationMapper {
    fun mapToVerificationEntity(response: Response<VerificationResponse>): VerificationResult {
        return if (response.isSuccessful) {
            val body = requireNotNull(response.body())
             VerificationSuccess(
                email = body.users[0].email,
                localId = body.users[0].localId,
                displayName = body.users[0].displayName,
                passwordHash = body.users[0].passwordHash,
                emailVerified = body.users[0].emailVerified,
                providerId = body.users[0].providerUserInfo[0].providerId,
                federatedId = body.users[0].providerUserInfo[0].federatedId,
            )
        }else {
            val body = requireNotNull(response.errorBody())
            val errorResponse: ErrorResponse =
                Gson().fromJson(body.string(), ErrorResponse::class.java)
            return VerificationError(errorResponse.error.message)
        }
    }
}
