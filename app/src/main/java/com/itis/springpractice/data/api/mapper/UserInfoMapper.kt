package com.itis.springpractice.data.api.mapper

import com.google.gson.Gson
import com.itis.springpractice.data.response.AuthErrorResponse
import com.itis.springpractice.data.response.UserInfoResponse
import com.itis.springpractice.domain.entity.UserInfoError
import com.itis.springpractice.domain.entity.UserInfoResult
import com.itis.springpractice.domain.entity.UserInfoSuccess
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
            val authErrorResponse: AuthErrorResponse =
                Gson().fromJson(body.string(), AuthErrorResponse::class.java)
            return UserInfoError(authErrorResponse.authError.message)
        }
    }
}
