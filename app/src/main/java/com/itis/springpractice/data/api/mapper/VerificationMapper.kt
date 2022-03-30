package com.itis.springpractice.data.api.mapper

import com.itis.springpractice.data.response.VerificationResponse
import com.itis.springpractice.domain.entity.*

class VerificationMapper {
    fun mapToVerificationEntity(response: Result<VerificationResponse>): VerificationResult {
        response.fold(onSuccess = {
            return VerificationSuccess(
                email = it.users[0].email,
                localId = it.users[0].localId,
                displayName = it.users[0].displayName,
                passwordHash = it.users[0].passwordHash,
                emailVerified = it.users[0].emailVerified,
                providerId = it.users[0].providerUserInfo[0].providerId,
                federatedId = it.users[0].providerUserInfo[0].federatedId,
            )
        }, onFailure = {
            return VerificationError(it.message)
        })
    }
}
