package com.itis.springpractice.data.database.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.itis.springpractice.data.response.UserResponse
import kotlinx.coroutines.tasks.await

class Firestore {
    private val db = Firebase.firestore

    private val usersRef = db.collection("users")

    private val storageRef = Firebase.storage.reference

    suspend fun uploadAvatar(nickname: String, data: ByteArray) {
        storageRef.child("users/${nickname}")
            .putBytes(data)
            .await()
    }

    suspend fun downloadAvatar(nickname: String): ByteArray? {
        return try {
            storageRef.child("users/${nickname}")
                .getBytes(1024 * 1024)
                .await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addUser(user: UserResponse) {
        user.nickname?.let {
            usersRef.document(it).set(user).await()
        }
    }

    suspend fun getUserByNickname(nickname: String): UserResponse? {
        return try {
            usersRef.document(nickname).get().await().toObject()
        } catch (e: Exception) {
            null
        }
    }

    private val friendsRef = db.collection("friends")
    suspend fun addFriend(userNickname: String, nicknameFriend: String) {
        val friendship = hashMapOf(
            "nickname_user" to userNickname,
            "nickname_friend" to nicknameFriend
        )
        friendsRef.add(friendship).await()
    }

    private suspend fun friendsNames(userNickname: String): List<String> {
        return friendsRef
            .whereEqualTo("nickname_user", userNickname)
            .get()
            .await()
            .map { it["nickname_friend"].toString() }
    }

    suspend fun getFriends(userNickname: String): List<UserResponse?> {
        return try {
            usersRef
                .whereIn("nickname", friendsNames(userNickname))
                .get()
                .await()
                .map { it.toObject() }
        } catch (e: Exception) {
            ArrayList()
        }
    }

    suspend fun isUserFriend(userNickname: String, friendNickname: String): Boolean {
        return friendsNames(userNickname).contains(friendNickname)
    }

    private suspend fun getNumberOfFriends(userNickname: String): Int {
        return friendsNames(userNickname).size
    }

    suspend fun getNumberOf(nickname: String): HashMap<String, Int> {
        //TODO("methods for reviews and collections number")
        return hashMapOf(
            "friends" to getNumberOfFriends(nickname),
            "reviews" to 0,
            "collections" to 0
        )
    }

    suspend fun updateUser(
        userNickname: String,
        firstName: String,
        lastName: String,
        data: ByteArray
    ) {
        usersRef.document(userNickname).update(
            "firstName", firstName,
            "lastName", lastName
        ).await()
        uploadAvatar(userNickname, data)
    }

    suspend fun deleteFriend(userNickname: String, friendNickname: String) {
        friendsRef.whereEqualTo("nickname_user", userNickname)
            .whereEqualTo("nickname_friend", friendNickname).get().await().map {
                it.reference.delete()
            }
    }

    companion object {
        private const val USER = "nickname_user"
        private const val FRIEND = "nickname_friend"
    }
}
