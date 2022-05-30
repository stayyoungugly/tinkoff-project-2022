package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentPlaceReviewBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.PlaceReviewViewModel
import timber.log.Timber

class PlaceReviewFragment : Fragment(R.layout.fragment_place_review) {
    private val binding by viewBinding(FragmentPlaceReviewBinding::bind)

    private val placeReviewViewModel by viewModels<PlaceReviewViewModel> {
        AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences),
            UserContainer(sharedPreferences)
        )
    }

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
    }

    private fun initObservers() {
        placeReviewViewModel.reviewAdded.observe(viewLifecycleOwner) {
            if (it) {
                showMessage(getString(R.string.review_was_added_success))
            } else showMessage(getString(R.string.review_was_added_failure))
        }

        placeReviewViewModel.reviewList.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                if (it.isEmpty()) {
                    showMessage(getString(R.string.empty_reviews))
                } else println(it.toString())
            }, onFailure = {
                Timber.e(it)
                showMessage(getString(R.string.failure_get_reviews))
            })
        }

        placeReviewViewModel.error.observe(viewLifecycleOwner) {
            Timber.e(it)
            showMessage(getString(R.string.try_again_error))
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

}
