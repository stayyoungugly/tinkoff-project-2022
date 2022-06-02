package com.itis.springpractice.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.domain.usecase.review.AddReviewOnPlaceUseCase
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
    private val getUserByNicknameUseCase: GetUserByNicknameUseCase
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
                        if (generateReview(textReview, rating)?.let {
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
                        println(ex.message)
                    }
                }
            }
        }
    }

    private suspend fun generateReview(textReview: String, rating: Float): Review? {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
        val formatted = current.format(formatter)
        var author: User? = null
        try {
            author = getUserByNicknameUseCase(getUserNicknameUseCase())
        } catch (ex: Exception) {
            _error.value = ex
            println(ex.message)
        }
        return author?.let { Review(it, textReview, rating, formatted) }
    }

    private val _reviewList: MutableLiveData<Result<List<Review>>> = MutableLiveData()
    val reviewList: LiveData<Result<List<Review>>> = _reviewList

    fun getReviewsByPlace(uri: String) {
        viewModelScope.launch {
            try {
                _reviewList.value = Result.success(getReviewsByPlaceUseCase(uri))
            } catch (ex: Exception) {
                _reviewList.value = Result.failure(ex)
                _error.value = ex
            }
        }
    }
}
