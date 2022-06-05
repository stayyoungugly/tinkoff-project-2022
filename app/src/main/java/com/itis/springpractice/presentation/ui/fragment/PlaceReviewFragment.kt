package com.itis.springpractice.presentation.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.DialogAddReviewBinding
import com.itis.springpractice.databinding.FragmentPlaceReviewBinding
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.presentation.ui.rv.ReviewsAdapter
import com.itis.springpractice.presentation.viewmodel.PlaceReviewViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class PlaceReviewFragment(uri: String) : Fragment(R.layout.fragment_place_review) {
    private val binding by viewBinding(FragmentPlaceReviewBinding::bind)

    private val uriPlace = uri

    private lateinit var dialogBinding: DialogAddReviewBinding

    private lateinit var reviewsAdapter: ReviewsAdapter

    private lateinit var review: Review

    private val placeReviewViewModel: PlaceReviewViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        placeReviewViewModel.getReviewsByPlace(uriPlace)
        binding.btnReview.setOnClickListener {
            navigateToAddReview()
        }
    }

    private fun initObservers() {
        placeReviewViewModel.reviewList.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                if (it.isEmpty()) {
                    binding.tvNoReviews.visibility = View.VISIBLE
                    binding.rvReviews.visibility = View.GONE
                } else {
                    binding.tvNoReviews.visibility = View.GONE
                    binding.rvReviews.visibility = View.VISIBLE
                    reviewsAdapter = ReviewsAdapter { nickname ->
                        navigateToFriendInfo(nickname)
                    }
                    binding.rvReviews.adapter = reviewsAdapter
                    refreshList(it)
                }
            }, onFailure = {
                Timber.e(it)
                showMessage(getString(R.string.failure_get_reviews), false)
            })
        }

        placeReviewViewModel.error.observe(viewLifecycleOwner) {
            Timber.e(it)
            showMessage(getString(R.string.try_again_error), false)
        }
    }

    private fun refreshList(reviewsList: List<Review>) {
        reviewsAdapter.submitList(reviewsList.toMutableList())
    }

    private fun showMessage(message: String, isDialogAttached: Boolean) {
        val root: Any
        root = if (!isDialogAttached) {
            binding.root
        } else {
            dialogBinding.root
        }
        Snackbar.make(
            root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun navigateToAddReview() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Добавить отзыв")
            .setView(DialogAddReviewBinding.inflate(layoutInflater).let {
                dialogBinding = it
                it.root
            })
            .setPositiveButton("Отправить", null)
            .setNegativeButton("Отменить") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            with(dialogBinding) {
                placeReviewViewModel.reviewAdded.observe(viewLifecycleOwner) {
                    if (it == "OK") {
                        placeReviewViewModel.getReviewsByPlace(uriPlace)
                        dialog.dismiss()
                        showMessage("Отзыв был успешно отправлен", true)
                    } else {
                        showMessage(it, true)
                    }
                }
                placeReviewViewModel.addReviewOnPlace(
                    uriPlace,
                    etContent.text.toString(),
                    rbRating.rating
                )

            }
        }
    }

    private fun navigateToFriendInfo(nickname: String) {
        TODO("Not yet implemented")
    }
}
