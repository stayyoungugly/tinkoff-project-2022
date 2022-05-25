package com.itis.springpractice.data.database.remote

import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.itis.springpractice.data.request.Review
import com.itis.springpractice.data.request.User
import timber.log.Timber

class Firestore {
    private val db = Firebase.firestore

    private val usersRef = db.collection("users")

    private val placesRef = db.collection("places")

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

    fun addReviewOnPlace(placeURI: String, placeReview: Review): Boolean {
        return try {
            placeReview.authorNickname?.let {
                placesRef.document(placeURI).collection("reviews").document(
                    it
                ).set(placeReview)
            }
            true
        } catch (ex: Exception) {
            Timber.e(ex.toString())
            false
        }
    }

    fun getReviewsByPlace(placeURI: String): List<Review> {
        val placeRef = placesRef.document(placeURI)
        var reviews = ArrayList<Review>()
        await(placeRef.collection("reviews").get()).apply {
            reviews = this.map { review ->
                review.toObject<Review>()
            } as ArrayList<Review>
        }
        return reviews
    }
}
