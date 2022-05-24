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
}
