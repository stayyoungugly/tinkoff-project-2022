package com.itis.springpractice.data.database.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.itis.springpractice.domain.entity.User

class Firestore {
    private val db = Firebase.firestore

    private val usersRef = db.collection("users")
    fun addUser(user: User) {
        usersRef.document(user.nickname).set(user)
    }

    fun getUserByNickname(nickname: String): User? {
        var user: User? = null
        val dockRef = usersRef.document(nickname)
        dockRef.get().addOnSuccessListener {
            user = it.toObject<User>()
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

    fun getFriends(nickname_user: String): List<User> {
        val query = friendsRef.whereEqualTo("nickname_user", nickname_user)
        var list = ArrayList<String>()
        query.get().addOnSuccessListener { documents ->
            for (item in documents) {
                val friend: String = item.data.
                list.add()
            }
        }
    }
}
