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
        return try {
            usersRef.document(nickname).get().await().toObject()
        } catch (e: Exception) {
            null
        }
    }

    private val friendsRef = db.collection("friends")
    suspend fun addFriend(userNickname: String, nicknameFriend: String) {
        val friendship = hashMapOf(
            USER to userNickname,
            FRIEND to nicknameFriend
        )
        friendsRef.add(friendship).await()
    }

    private suspend fun friendsNames(userNickname: String): List<String> {
        return friendsRef
            .whereEqualTo(USER, userNickname)
            .get()
            .await()
            .map { it[FRIEND].toString() }
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

    suspend fun updateUser(user: UserResponse, userNickname: String) {
        usersRef.document(userNickname).set(user).await()
    }

    suspend fun update(user: UserResponse, userNickname: String) {
        friendsRef.whereEqualTo(USER, userNickname).get().await().map {
            it.reference.update(USER, user.nickname)
        }

        friendsRef.whereEqualTo(FRIEND, userNickname).get().await().map {
            it.reference.update(FRIEND, user.nickname)
        }
        //TODO("update for other collections")
    }

    companion object {
        private const val USER = "nickname_user"
        private const val FRIEND = "nickname_friend"
    }
}
