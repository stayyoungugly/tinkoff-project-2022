package com.itis.springpractice.data.database.remote

import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
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

    fun getReviewsByPlace(placeURI: String): List<ReviewResponse> {
        val placeRef = placesRef.document(placeURI)
        return await(placeRef.collection("reviews").get()).map { review ->
                review.toObject<ReviewResponse>()
            } as ArrayList<ReviewResponse>
        }
    }

