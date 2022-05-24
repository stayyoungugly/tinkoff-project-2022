package com.itis.springpractice.data.database.remote

import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.itis.springpractice.data.response.User

class Firestore {
    private val db = Firebase.firestore

    private val usersRef = db.collection("users")

    fun addUser(user: User) {
        user.nickname?.let {
            usersRef.document(it).set(user)
        }
    }

    fun getUserByNickname(nickname: String): User? {
        var user: User?
        val dockRef = usersRef.document(nickname)
        await(dockRef.get()).apply {
            user = this.toObject<User>()
        }
        return user
    }

    private val friendsRef = db.collection("friends")
    fun addFriend(nickname_user: String, nickname_friend: String) {
        val friendship = hashMapOf(
            "nickname_user" to nickname_user,
            "nickname_friend" to nickname_friend
        )
        friendsRef.add(friendship)
    }

    fun getFriends(nickname_user: String): List<User?> {
        val query = friendsRef.whereEqualTo("nickname_user", nickname_user)
        var users = ArrayList<User?>()
        await(query.get()).apply {
            val list = this.map { snapshot ->
                snapshot["nickname_friend"].toString()
            }
            users = list.map { nickname ->
                getUserByNickname(nickname)
            } as ArrayList<User?>
        }
        return users
    }
}
