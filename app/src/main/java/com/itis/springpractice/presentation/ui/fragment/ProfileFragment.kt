package com.itis.springpractice.presentation.ui.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentProfileBinding
import com.itis.springpractice.presentation.ui.fragment.extension.findParent
import com.itis.springpractice.presentation.viewmodel.ProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val binding by viewBinding(FragmentProfileBinding::bind)

    private val profileViewModel: ProfileViewModel by viewModel()

    private val nicknameProfile: String? by lazy {
        arguments?.getString("nickname")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        profileViewModel.onGetUserInfo(nicknameProfile)
        profileViewModel.onGetNumberOf(nicknameProfile)
        binding.clFriends.setOnClickListener {
            val bundle = Bundle().apply {
                putString("nickname", nicknameProfile)
            }
            findNavController().navigate(R.id.action_profileFragment_to_friendsFragment, bundle)
        }
        binding.clReviews.setOnClickListener {
            val bundle = Bundle().apply {
                putString("nickname", nicknameProfile)
            }
            findNavController().navigate(R.id.action_profileFragment_to_reviewsFragment, bundle)
        }
        binding.clCollections.setOnClickListener {
            val bundle = Bundle().apply {
                putString("nickname", nicknameProfile)
            }
            findNavController().navigate(R.id.action_profileFragment_to_favouritesFragment, bundle)
        }
    }

    private fun initObservers() {
        profileViewModel.user.observe(viewLifecycleOwner) {
            it.fold(onSuccess = {
                with(binding) {
                    this.tvName.text = "${it.firstName} ${it.lastName}"
                    this.tvNickname.text = it.nickname
                    if (it.avatar == null) {
                        val bitmap = BitmapFactory.decodeResource(
                            requireContext().resources,
                            R.drawable.no_avatar
                        )
                        this.ivPhoto.setImageBitmap(bitmap)
                    } else {
                        val bitmap = BitmapFactory.decodeByteArray(it.avatar, 0, it.avatar.size)
                        this.ivPhoto.setImageBitmap(bitmap)
                    }
                }
            }, onFailure = {
                showMessage("Проверьте подключение к интернету")
            })
        }
        profileViewModel.isUser.observe(viewLifecycleOwner) {
            if (it) {
                binding.fabEdit.setOnClickListener {
                    (this.findParent<AuthorizedFragment>() as? Callbacks)?.navigateToEdit()
                }
                binding.btnSignOut.setOnClickListener {
                    onSignOutClick()
                }
            } else {
                binding.fabEdit.visibility = GONE
                binding.btnSignOut.visibility = GONE
            }
        }

        profileViewModel.number.observe(viewLifecycleOwner) {
            with(binding) {
                this.tvFriendsNumber.text = it["friends"].toString()
                this.tvReviewNumber.text = it["reviews"].toString()
                this.tvCollectionsNumber.text = it["likes"].toString()
            }
        }
    }

    private fun onSignOutClick() {
        profileViewModel.onDeleteClick()
        (this.findParent<AuthorizedFragment>() as? Callbacks)?.navigateToSignIn()
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
