package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.usecase.review.AddReviewOnPlaceUseCase
import com.itis.springpractice.domain.usecase.review.DeleteReviewUseCase
import com.itis.springpractice.domain.usecase.review.GetReviewsByPlaceUseCase
import com.itis.springpractice.domain.usecase.user.GetUserByNicknameUseCase
import com.itis.springpractice.domain.usecase.user.GetUserNicknameUseCase
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PlaceReviewViewModel(
    private val getReviewsByPlaceUseCase: GetReviewsByPlaceUseCase,
    private val addReviewOnPlaceUseCase: AddReviewOnPlaceUseCase,
    private val getUserNicknameUseCase: GetUserNicknameUseCase,
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase,
    private val deleteReviewUseCase: DeleteReviewUseCase
) : ViewModel() {

    private val _error: MutableLiveData<Throwable> = MutableLiveData()
    val error: LiveData<Throwable> = _error

    private val _reviewAdded: SingleLiveEvent<String> = SingleLiveEvent()
    val reviewAdded: LiveData<String> = _reviewAdded

    fun addReviewOnPlace(uri: String, textReview: String, rating: Float) {
        viewModelScope.launch {
            when {
                rating == 0f -> {
                    _reviewAdded.value = "Поставьте оценку"
                }
                textReview.isEmpty() -> {
                    _reviewAdded.value = "Оставьте отзыв"
                }
                textReview.length >= 200 -> {
                    _reviewAdded.value = "Отзыв слишком длинный"
                }
                textReview.length < 5 -> {
                    _reviewAdded.value = "Отзыв слишком короткий"
                }
                else -> {
                    try {
                        if (generateReview(uri, textReview, rating)?.let {
                                addReviewOnPlaceUseCase(
                                    uri,
                                    it
                                )
                            }
                            == true
                        ) {
                            _reviewAdded.value = "OK"
                        }
                    } catch (ex: Exception) {
                        _reviewAdded.value = "Ошибка! Попробуйте еще раз"
                        _error.value = ex
                    }
                }
            }
        }
    }

    private suspend fun generateReview(uri: String, textReview: String, rating: Float): Review? {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
        val formatted = current.format(formatter)
        var author: User? = null
        try {
            author = getUserByNicknameUseCase(getUserNicknameUseCase())
        } catch (ex: Exception) {
            _error.value = ex
        }
        return author?.let { Review(null, uri.drop(19), it, textReview, rating, formatted) }
    }

    private val _nickname: SingleLiveEvent<String> = SingleLiveEvent()
    val nickname: LiveData<String> = _nickname

    fun getUserNickname() {
        viewModelScope.launch {
            try {
                _nickname.value = getUserNicknameUseCase()
            } catch (ex: Exception) {
                _error.value = ex
            }
        }
    }

    fun deleteReview(uri: String) {
        viewModelScope.launch {
            try {
                deleteReviewUseCase(getUserNicknameUseCase(), uri)
                val list = _reviewList.value as ArrayList<Review>
                for (item in list) {
                    if (item.uri == uri) {
                        list.remove(item)
                        _reviewList.value = list
                    }
                }
            } catch (ex: Exception) {
                _error.value = ex
            }
        }
    }

    private val _reviewList: SingleLiveEvent<List<Review>> = SingleLiveEvent()
    val reviewList: LiveData<List<Review>> = _reviewList

    fun getReviewsByPlace(uri: String) {
        viewModelScope.launch {
            try {
                val reviews = getReviewsByPlaceUseCase(uri)
                for (review in reviews) {
                    if (review.author.nickname == getUserNicknameUseCase()) {
                        review.author.nickname = "${review.author.nickname} (вы)"
                    }
                }
                _reviewList.value = reviews
            } catch (ex: Exception) {
                _error.value = ex
            }
        }
    }
}
