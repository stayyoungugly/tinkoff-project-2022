package com.itis.springpractice.data.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.ErrorResponse
import com.itis.springpractice.data.response.UserInfoResponse
import com.itis.springpractice.domain.entity.*
import retrofit2.Response

class UserInfoMapper {
    fun mapToVerificationEntity(response: Response<UserInfoResponse>): UserInfoResult {
        return if (response.isSuccessful) {
            val body = requireNotNull(response.body())
            UserInfoSuccess(
                email = body.userAccounts[0].email,
                localId = body.userAccounts[0].localId,
                emailVerified = body.userAccounts[0].emailVerified,
                createdAt = body.userAccounts[0].createdAt,
            )
        } else {
            val body = requireNotNull(response.errorBody())
            val errorResponse: ErrorResponse =
                Gson().fromJson(body.string(), ErrorResponse::class.java)
            return UserInfoError(errorResponse.error.message)
        }
    }
}
