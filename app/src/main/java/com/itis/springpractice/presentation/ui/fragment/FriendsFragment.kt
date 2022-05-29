package com.itis.springpractice.presentation.ui.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentFriendsBinding
import com.itis.springpractice.di.FriendContainer
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.ui.rv.FriendsAdapter
import com.itis.springpractice.presentation.viewmodel.FriendsViewModel

class FriendsFragment : Fragment(R.layout.fragment_friends) {
    private val binding by viewBinding(FragmentFriendsBinding::bind)

    private val friendsViewModel by viewModels<FriendsViewModel> {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendsViewModel.onGetFriends()
        binding.fabAddFriend.setOnClickListener {
            navigateToAddFriend()
        }
        initObservers()
    }

    private fun initObservers() {
        friendsViewModel.friends.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                if (it.isEmpty()) {
                    binding.tvZeroFriends.visibility = VISIBLE
                    binding.rvFriends.visibility = GONE
                } else {
                    binding.tvZeroFriends.visibility = GONE
                    binding.rvFriends.visibility = VISIBLE
                    val friendsAdapter = FriendsAdapter(it) { friendNickname ->
                        navigateToFriendInfo(friendNickname)
                    }
                    binding.rvFriends.adapter = friendsAdapter
                }
            }, onFailure = {
                showMessage("Проверьте подключение к интернету")
            })
        }
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun navigateToAddFriend() {
        AddFriendDialog.show(
            childFragmentManager,
            positive = {
                showMessage(it)
                TODO("Update rv")
            }
        )
    }

    private fun navigateToFriendInfo(friendNickname: String) {
        TODO("Not yet implemented")
    }
}
