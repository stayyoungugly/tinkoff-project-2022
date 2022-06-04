package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentProfileBinding
import com.itis.springpractice.di.FriendContainer
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.ui.fragment.extension.findParent
import com.itis.springpractice.presentation.viewmodel.ProfileViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val binding by viewBinding(FragmentProfileBinding::bind)

    private val profileViewModel by viewModels<ProfileViewModel> {
        AuthFactory(
            UserAuthContainer,
            UserTokenContainer(sharedPreferences),
            UserContainer(sharedPreferences),
            FriendContainer(sharedPreferences)
        )
    }

    private val sharedPreferences by lazy {
        requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    private val nickname: String? by lazy {
        arguments?.getString("nickname")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        profileViewModel.onGetUserInfo(nickname)
        profileViewModel.onGetNumberOf(nickname)
        binding.clFriends.setOnClickListener {
            val bundle = Bundle().apply {
                putString("nickname", nickname)
            }
            findNavController().navigate(R.id.action_profileFragment_to_friendsFragment, bundle)
        }
        //TODO("click and navigate to reviews and collections")
    }

    private fun initObservers() {
        profileViewModel.user.observe(viewLifecycleOwner) {
            it.fold(onSuccess = {
                with(binding) {
                    this.tvName.text = "${it.firstName} ${it.lastName}"
                    this.tvNickname.text = it.nickname
                    //TODO("set image")
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
            }
            else {
                binding.fabEdit.visibility = GONE
            }
        }

        profileViewModel.number.observe(viewLifecycleOwner) {
            with(binding) {
                this.tvFriendsNumber.text = it["friends"].toString()
                this.tvReviewNumber.text = it["reviews"].toString()
                this.tvCollectionsNumber.text = it["collections"].toString()
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
