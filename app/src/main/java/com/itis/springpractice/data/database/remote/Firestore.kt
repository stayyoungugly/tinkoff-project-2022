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
}
