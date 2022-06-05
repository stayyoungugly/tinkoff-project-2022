package com.itis.springpractice.presentation.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.DialogAddFriendBinding
import com.itis.springpractice.databinding.FragmentFriendsBinding
import com.itis.springpractice.presentation.ui.rv.FriendsAdapter
import com.itis.springpractice.presentation.viewmodel.FriendsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FriendsFragment : Fragment(R.layout.fragment_friends) {
    private val binding by viewBinding(FragmentFriendsBinding::bind)
    private lateinit var dialogBinding: DialogAddFriendBinding

    private lateinit var friendsAdapter: FriendsAdapter

    private val friendsViewModel: FriendsViewModel by viewModel()

    private val nicknameFriend: String? by lazy {
        arguments?.getString("nickname")
    }

    private var isUser: Boolean = nicknameFriend == null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        friendsViewModel.onGetFriends(nicknameFriend)
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
                    friendsAdapter = FriendsAdapter(
                        { nickname ->
                            navigateToFriendProfile(nickname)
                        },
                        { nickname -> deleteFriend(nickname) },
                        isUser
                    )
                    binding.rvFriends.adapter = friendsAdapter
                    friendsAdapter.submitList(it.toMutableList())
                }
            }, onFailure = {
                showMessage("Проверьте подключение к интернету")
            })
        }
        friendsViewModel.isUser.observe(viewLifecycleOwner) {
            isUser = it
            binding.fabAddFriend.apply {
                if (it) {
                    setOnClickListener {
                        navigateToAddFriend()
                    }
                } else {
                    visibility = GONE
                    binding.tvZeroFriends.text = context.getString(R.string.no_friends)
                }
            }
        }
    }

    private fun deleteFriend(nickname: String) {
        friendsViewModel.onDeleteFriend(nickname)
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
            .setTitle("Подписаться")
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
                } else {
                    dialogBinding.etNickname.error = it
                }
            }
            friendsViewModel.onAddFriend(dialogBinding.etNickname.text.toString())
        }
    }

    private fun navigateToFriendProfile(friendNickname: String) {
        val bundle = Bundle().apply {
            putString("nickname", friendNickname)
        }
        findNavController().navigate(R.id.action_friendsFragment_to_profileFragment, bundle)
    }
}
