package com.itis.springpractice.data.impl

import com.itis.springpractice.data.database.local.PreferenceManager
import com.itis.springpractice.data.database.remote.Firestore
import com.itis.springpractice.data.mapper.UserModelMapper
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.repository.UserRepository

class UserRepositoryImpl(
    private val firestore: Firestore,
    private val userModelMapper: UserModelMapper,
    private val preferenceManager: PreferenceManager
) : UserRepository {

    companion object {
        private const val DEFAULT_VALUE = ""
    }

    override suspend fun addUser(user: User, email: String) {
        firestore.addUser(userModelMapper.mapToUserResponse(user), email)
        preferenceManager.saveNickname(user.nickname)
    }

    override suspend fun getUserByNickname(nickname: String): User? {
        val userFirestore = firestore.getUserByNickname(nickname)
        val downloadAvatar = firestore.downloadAvatar(nickname)
        return userFirestore?.let {
            userModelMapper.mapToUser(it, downloadAvatar)
        }
    }

    override suspend fun getUserNickname(): String {
        println(preferenceManager.getNickname() ?: DEFAULT_VALUE)
        return preferenceManager.getNickname() ?: DEFAULT_VALUE
    }

    override suspend fun addUserLike(nickname: String, uri: String) {
        firestore.addLike(nickname, uri)
    }

    override suspend fun isPlaceLiked(nickname: String, uri: String): String? {
        return firestore.isPlaceLiked(nickname, uri)?.uri
    }

    override suspend fun deleteUserLike(nickname: String, uri: String) {
        firestore.deleteLike(nickname, uri)
    }

    override suspend fun deleteNickname() {
        preferenceManager.deleteNickname()
    }

    override suspend fun getNumberOf(nickname: String): HashMap<String, Int> {
        return firestore.getNumberOf(nickname)
    }

    override suspend fun getLikes(nickname: String): List<String> {
        return firestore.getLikedPlaces(nickname)
    }

    override suspend fun updateUser(firstName: String, lastName: String, uploadAvatar: ByteArray) {
        val userNickname = preferenceManager.getNickname() ?: DEFAULT_VALUE
        firestore.updateUser(userNickname, firstName, lastName, uploadAvatar)
    }

    override suspend fun updateNickname(email: String) {
        preferenceManager.saveNickname(firestore.getNicknameByEmail(email))
    }
}
