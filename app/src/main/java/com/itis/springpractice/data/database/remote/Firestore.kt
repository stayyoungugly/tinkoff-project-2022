package com.itis.springpractice.data.database.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.itis.springpractice.data.response.UserResponse
import kotlinx.coroutines.tasks.await

class Firestore {
    private val db = Firebase.firestore

    private val usersRef = db.collection("users")

    suspend fun addUser(user: UserResponse) {
        user.nickname?.let {
            usersRef.document(it).set(user).await()
        }
    }

    suspend fun getUserByNickname(nickname: String): UserResponse? {
        //return usersRef.document(nickname).get().await().toObject()
        return try {
            usersRef.document(nickname).get().await().toObject()
        } catch (e: Exception) {
            null
        }
    }

    private val friendsRef = db.collection("friends")
    suspend fun addFriend(nickname_user: String, nickname_friend: String) {
        val friendship = hashMapOf(
            "nickname_user" to nickname_user,
            "nickname_friend" to nickname_friend
        )
        friendsRef.add(friendship).await()
    }

    private suspend fun friendsNames(nickname_user: String): List<String> {
        return friendsRef
            .whereEqualTo("nickname_user", nickname_user)
            .get()
            .await()
            .map { it["nickname_friend"].toString() }
    }

    suspend fun getFriends(nickname_user: String): List<UserResponse?> {
        return try {
            usersRef
                .whereIn("nickname", friendsNames(nickname_user))
                .get()
                .await()
                .map { it.toObject() }
        } catch (e: Exception) {
            ArrayList()
        }
    }

    suspend fun isUserFriend(nickname_user: String, nickname_friend: String): Boolean {
        if (friendsNames(nickname_user).contains(nickname_friend)) {
            return true
        }
        return false
    }
}
