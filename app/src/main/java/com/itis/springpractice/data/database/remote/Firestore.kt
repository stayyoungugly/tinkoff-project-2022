package com.itis.springpractice.data.database.remote

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.itis.springpractice.data.response.LikeResponse
import com.itis.springpractice.data.response.ReviewResponse
import com.itis.springpractice.data.response.UserResponse
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class Firestore {
    private val db = Firebase.firestore

    private val usersRef = db.collection("users")

    private val placesRef = db.collection("places")

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
            "nickname_user" to userNickname,
            "nickname_friend" to nicknameFriend
        )
        friendsRef.add(friendship).await()
    }

    private suspend fun friendsNames(userNickname: String): List<String> {
        return friendsRef
            .whereEqualTo("nickname_user", userNickname)
            .get()
            .await()
            .map { it["nickname_friend"].toString() }
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

    suspend fun isPlaceLiked(nickname: String, placeURI: String): LikeResponse? {
        val dockRef = usersRef.document(nickname)
        return dockRef.collection("likes").document(placeURI).get().await().toObject<LikeResponse>()
    }

    suspend fun getLikedPlaces(nickname: String): List<String> {
        val dockRef = usersRef.document(nickname)
        return dockRef.collection("likes").get().await().map { like ->
            like.toString()
        } as ArrayList<String>
    }

    suspend fun getReviewsByPlace(placeURI: String): List<ReviewResponse> {
        val placeRef = placesRef.document(placeURI)
        return placeRef.collection("reviews").get().await().map { review ->
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

