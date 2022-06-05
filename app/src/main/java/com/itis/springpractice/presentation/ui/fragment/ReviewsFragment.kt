package com.itis.springpractice.presentation.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentUserReviewsBinding
import com.itis.springpractice.domain.entity.Place
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.presentation.ui.rv.ReviewsAdapter
import com.itis.springpractice.presentation.viewmodel.UserReviewsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ReviewsFragment : Fragment(R.layout.fragment_user_reviews) {
    private val binding by viewBinding(FragmentUserReviewsBinding::bind)

    private lateinit var reviewsAdapter: ReviewsAdapter

    private val userReviewsViewModel: UserReviewsViewModel by viewModel()

    private val glide: RequestManager by lazy {
        Glide.with(this)
    }

    private val nicknameProfile: String? by lazy {
        arguments?.getString("nickname")
    }

    private var nickname = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        userReviewsViewModel.getUserNickname()

    }

    private fun initObservers() {
        userReviewsViewModel.nickname.observe(viewLifecycleOwner) {
            if (it != null) {
                nickname = it
                userReviewsViewModel.onGetReviews(nicknameProfile)
            }
        }
        userReviewsViewModel.reviews.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.tvZeroReviews.visibility = View.VISIBLE
                binding.rvReviews.visibility = View.GONE
            } else {
                binding.tvZeroReviews.visibility = View.GONE
                binding.rvReviews.visibility = View.VISIBLE
                reviewsAdapter = ReviewsAdapter(
                    glide, { item -> navigateToPlace(item) },
                    { uri -> deleteReview(uri) },
                    nickname
                )
                binding.rvReviews.adapter = reviewsAdapter
                refreshList(it)
            }
        }

        userReviewsViewModel.error.observe(viewLifecycleOwner) {
            Timber.e(it)
            showMessage(getString(R.string.try_again_error))
        }
    }

    private fun deleteReview(uri: String) {
        userReviewsViewModel.deleteReview(uri)
    }

    private fun refreshList(reviewsList: List<Review>) {
        reviewsAdapter.submitList(reviewsList.toMutableList())
    }

    private fun navigateToPlace(place: Place) {
        val bundle = Bundle().apply {
            place.latitude?.let { putDouble("latitude", it) }
            place.longitude?.let { putDouble("longitude", it) }
            putString("uri", place.uri)
        }
        findNavController().navigate(R.id.action_reviewsFragment_to_mapFragment, bundle)
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}

