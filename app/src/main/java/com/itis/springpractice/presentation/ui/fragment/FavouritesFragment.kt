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
import com.itis.springpractice.databinding.FragmentFavouritesBinding
import com.itis.springpractice.domain.entity.Place
import com.itis.springpractice.presentation.ui.rv.PlacesAdapter
import com.itis.springpractice.presentation.viewmodel.PlacesFavouriteViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {
    private val binding by viewBinding(FragmentFavouritesBinding::bind)

    private lateinit var placesAdapter: PlacesAdapter

    private val placesFavouriteViewModel: PlacesFavouriteViewModel by viewModel()

    private val glide: RequestManager by lazy {
        Glide.with(this)
    }

    private var isUser = false

    private val nickname: String? by lazy {
        arguments?.getString("nickname")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        placesFavouriteViewModel.onGetFavourites(nickname)
    }

    private fun initObservers() {
        placesFavouriteViewModel.isUser.observe(viewLifecycleOwner) {
            if (it != null) isUser = it
        }
        placesFavouriteViewModel.favourites.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.tvZeroPlaces.visibility = View.VISIBLE
                binding.rvPlaces.visibility = View.GONE
            } else {
                binding.tvZeroPlaces.visibility = View.GONE
                binding.rvPlaces.visibility = View.VISIBLE
                placesAdapter = PlacesAdapter(
                    glide,
                    { point ->
                        navigateToPlace(point)
                    },
                    { nickname -> deletePlace(nickname) },
                    isUser
                )
                binding.rvPlaces.adapter = placesAdapter
                placesAdapter.submitList(it.toMutableList())
            }
        }

        placesFavouriteViewModel.error.observe(viewLifecycleOwner) {
            if (it != null) {
                Timber.e(it)
                showMessage(getString(R.string.favourites_not_found))
            }
        }
    }

    private fun deletePlace(uri: String) {
        placesFavouriteViewModel.deleteFromLikes(uri)
    }

    private fun navigateToPlace(place: Place) {
        val bundle = Bundle().apply {
            place.latitude?.let { putDouble("latitude", it) }
            place.longitude?.let { putDouble("longitude", it) }
            putString("uri", place.uri)
        }
        findNavController().navigate(R.id.action_favouritesFragment_to_mapFragment, bundle)
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}

