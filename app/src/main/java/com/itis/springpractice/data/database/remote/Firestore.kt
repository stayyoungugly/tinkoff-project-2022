package com.itis.springpractice.data.database.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.itis.springpractice.data.response.UserResponse
import kotlinx.coroutines.tasks.await

class Firestore {
    private val db = Firebase.firestore

    private val usersRef = db.collection("users")

    fun addUser(user: UserResponse) {
        user.nickname?.let {
            usersRef.document(it).set(user)
        }
    }

    suspend fun getUserByNickname(nickname: String): UserResponse? {
        return usersRef.document(nickname).get().await().toObject()
    }

    private val friendsRef = db.collection("friends")
    fun addFriend(nickname_user: String, nickname_friend: String) {
        val friendship = hashMapOf(
            "nickname_user" to nickname_user,
            "nickname_friend" to nickname_friend
        )
        friendsRef.add(friendship)
    }

    suspend fun getFriends(nickname_user: String): List<UserResponse?> {
        val friendsNames = friendsRef
            .whereEqualTo("nickname_user", nickname_user)
            .get()
            .await()
            .map { it["nickname_friend"].toString() }
        return usersRef
            .whereIn("nickname", friendsNames)
            .get()
            .await()
            .map { it.toObject() }
    }
}
