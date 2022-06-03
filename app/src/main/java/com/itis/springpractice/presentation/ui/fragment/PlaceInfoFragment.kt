package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentPlaceInfoBinding
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.domain.entity.Place
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.PlaceInfoViewModel
import timber.log.Timber

class PlaceInfoFragment(uri: String) : Fragment(R.layout.fragment_place_info) {

    private val uriPlace = uri

    private val binding by viewBinding(FragmentPlaceInfoBinding::bind)

    private val glideOptions by lazy {
        RequestOptions()
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    private val glide: RequestManager by lazy {
        Glide.with(this)
    }

    private val placeInfoViewModel by viewModels<PlaceInfoViewModel> {
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
        placeInfoViewModel.searchGeoObjectInfo(uriPlace)
    }

    private fun initObservers() {
        placeInfoViewModel.place.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                setPlaceInfo(it)
            }, onFailure = {
                showMessage("Ошибка! Повторите еще раз")
            })
        }

        placeInfoViewModel.error.observe(viewLifecycleOwner) {
            Timber.e(it)
            showMessage(getString(R.string.info_not_found))
        }
    }

    private fun setPlaceInfo(place: Place) {
        with(binding) {
            tvAddress.text = place.address
            tvHours.text = place.workingHours
            tvPhone.text = place.phones
            if (place.closed) {
                tvClosed.text = getString(R.string.closed)
                tvClosed.setTextColor(resources.getColor(R.color.red, activity?.theme))
            }
            if (!place.photoUrl.isNullOrEmpty()) {
                glide.load(place.photoUrl)
                    .apply(glideOptions)
                    .into(ivPhoto)
            }
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
