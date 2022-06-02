package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
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
        binding.textView.text = place.name
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
