package com.itis.springpractice.data.database.remote

import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.itis.springpractice.data.response.UserResponse

class Firestore {
    private val db = Firebase.firestore

    private val usersRef = db.collection("users")

    fun addUser(user: UserResponse) {
        user.nickname?.let {
            usersRef.document(it).set(user)
        }
    }

    fun getUserByNickname(nickname: String): UserResponse? {
        val dockRef = usersRef.document(nickname)
        return await(dockRef.get()).toObject<UserResponse>()
    }
}
