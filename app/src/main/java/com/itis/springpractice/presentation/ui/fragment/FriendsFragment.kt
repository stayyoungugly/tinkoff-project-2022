package com.itis.springpractice.presentation.ui.fragment

import android.app.AlertDialog
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
import com.itis.springpractice.databinding.DialogAddFriendBinding
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
    private lateinit var dialogBinding: DialogAddFriendBinding

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
        initObservers()
        friendsViewModel.onGetFriends()
        binding.fabAddFriend.setOnClickListener {
            navigateToAddFriend()
        }
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
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Добавить новый")
            .setView(DialogAddFriendBinding.inflate(layoutInflater).let {
                dialogBinding = it
                it.root
            })
            .setPositiveButton("Добавить", null)
            .setNegativeButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            friendsViewModel.message.observe(viewLifecycleOwner) {
                if (it.equals("OK")) {
                    dialog.dismiss()
                }
                else {
                    dialogBinding.etNickname.error = it
                }
            }
            friendsViewModel.onAddFriend(dialogBinding.etNickname.text.toString())
        }
    }

    private fun navigateToFriendInfo(friendNickname: String) {
        TODO("Not yet implemented")
    }
}
