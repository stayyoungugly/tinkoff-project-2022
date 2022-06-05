package com.itis.springpractice.data.database.remote

import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.itis.springpractice.data.response.LikeResponse
import com.itis.springpractice.data.response.ReviewResponse
import com.itis.springpractice.data.response.UserResponse
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class Firestore {
    private val db = Firebase.firestore

    private val usersRef = db.collection("users")

    private val placesRef = db.collection("places")

    private val storageRef = Firebase.storage.reference

    private suspend fun uploadAvatar(nickname: String, data: ByteArray) {
        storageRef.child("users/${nickname}")
            .putBytes(data)
            .await()
    }

    suspend fun downloadAvatar(nickname: String): ByteArray? {
        return try {
            storageRef.child("users/${nickname}")
                .getBytes(1024 * 1024)
                .await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addUser(user: UserResponse, email: String) {
        user.nickname?.let {
            usersRef.document(it).set(user).await()
        }
        val data = hashMapOf(
            "email" to email
        )
        user.nickname?.let {
            usersRef.document(it).set(data, SetOptions.merge()).await()
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

    suspend fun deleteReview(nickname: String, uri: String) {
        usersRef.document(nickname).collection("reviews").document(uri).delete()
        placesRef.document(uri).collection("reviews").document(nickname).delete()
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
        return hashMapOf(
            "friends" to getNumberOfFriends(nickname),
            "reviews" to getUserReviews(nickname).size,
            "likes" to getLikedPlaces(nickname).size
        )
    }

    suspend fun updateUser(
        userNickname: String,
        firstName: String,
        lastName: String,
        data: ByteArray
    ) {
        usersRef.document(userNickname).update(
            "firstName", firstName,
            "lastName", lastName
        ).await()
        uploadAvatar(userNickname, data)
    }

    suspend fun deleteFriend(userNickname: String, friendNickname: String) {
        friendsRef.whereEqualTo("nickname_user", userNickname)
            .whereEqualTo("nickname_friend", friendNickname).get().await().map {
                it.reference.delete()
            }
    }

    companion object {
        private const val USER = "nickname_user"
        private const val FRIEND = "nickname_friend"
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

    suspend fun getUserReviews(nickname: String): List<ReviewResponse> {
        return usersRef.document(nickname).collection("reviews").get().await().map { review ->
            review.toObject<ReviewResponse>()
        } as ArrayList<ReviewResponse>
    }

    suspend fun isPlaceLiked(nickname: String, placeURI: String): LikeResponse? {
        val dockRef = usersRef.document(nickname)
        return dockRef.collection("likes").document(placeURI).get().await().toObject<LikeResponse>()
    }

    suspend fun getLikedPlaces(nickname: String): List<String> {
        val dockRef = usersRef.document(nickname)
        return dockRef.collection("likes")
            .get()
            .await()
            .map {
                it["uri"].toString()
            } as ArrayList<String>
    }

    suspend fun getReviewsByPlace(placeURI: String, userNickname: String): List<ReviewResponse> {
        val placeRef = placesRef.document(placeURI)
        val checkList = friendsNames(userNickname) as ArrayList<String>
        checkList.add(userNickname)
        return placeRef.collection("reviews")
            .whereIn("authorNickname", checkList)
            .get()
            .await()
            .map { review ->
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

    suspend fun getNicknameByEmail(email: String): String {
        var nickname = ""
        usersRef.whereEqualTo("email", email).get().await().map {
            nickname = it.reference.get().await().get("nickname").toString()
        }
        return nickname
    }
}

