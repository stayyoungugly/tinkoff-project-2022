package com.itis.springpractice.data.database.remote

import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.itis.springpractice.data.response.LikeResponse
import com.itis.springpractice.data.response.ReviewResponse
import com.itis.springpractice.data.response.UserResponse
import timber.log.Timber

class Firestore {
    private val db = Firebase.firestore

    private val usersRef = db.collection("users")

    private val placesRef = db.collection("places")

    fun addUser(user: UserResponse) {
        user.nickname?.let {
            usersRef.document(it).set(user)
        }
    }

    fun getUserByNickname(nickname: String): UserResponse? {
        val dockRef = usersRef.document(nickname)
        return await(dockRef.get()).toObject<UserResponse>()
    }

    fun addReviewOnPlace(placeURI: String, placeReview: ReviewResponse): Boolean {
        return try {
            placeReview.authorNickname?.let {
                placesRef.document(placeURI).collection("reviews").document(it).set(placeReview)
                usersRef.document(it).collection("reviews").document(placeURI).set(placeReview)
            }
            true
        } catch (ex: Exception) {
            Timber.e(ex.toString())
            false
        }
    }

    fun getUserReviews(nickname: String) {}

    fun isPlaceLiked(nickname: String, placeURI: String): LikeResponse? {
        val dockRef = usersRef.document(nickname)
        return await(dockRef.collection("likes").document(placeURI).get()).toObject<LikeResponse>()
    }

    fun getLikedPlaces(nickname: String): List<String> {
        val dockRef = usersRef.document(nickname)
        return await(dockRef.collection("likes").get()).map { like ->
            like.toString()
        } as ArrayList<String>
    }

    fun getReviewsByPlace(placeURI: String): List<ReviewResponse> {
        val placeRef = placesRef.document(placeURI)
        return await(placeRef.collection("reviews").get()).map { review ->
            review.toObject<ReviewResponse>()
        } as ArrayList<ReviewResponse>
    }

    fun addLike(nickname: String, placeURI: String) {
        val data = hashMapOf(
            "uri" to placeURI
        )
        usersRef.document(nickname).collection("likes").document(placeURI).set(data)
    }

    fun deleteLike(nickname: String, placeURI: String) {
        usersRef.document(nickname).collection("likes").document(placeURI).delete()
    }
}

