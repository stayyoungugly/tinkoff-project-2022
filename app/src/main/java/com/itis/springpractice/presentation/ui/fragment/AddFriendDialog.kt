package com.itis.springpractice.presentation.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.itis.springpractice.R
import com.itis.springpractice.databinding.DialogAddFriendBinding
import com.itis.springpractice.di.FriendContainer
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.AddFriendViewModel

class AddFriendDialog : DialogFragment(R.layout.dialog_add_friend) {
    //private val binding: DialogAddFriendBinding by viewBinding(createMethod = CreateMethod.INFLATE)
    private lateinit var binding: DialogAddFriendBinding

    private val addFriendViewModel by viewModels<AddFriendViewModel> {
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

    private lateinit var positiveCallback: (String) -> Unit

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setView(DialogAddFriendBinding.inflate(layoutInflater).let {
                binding = it
                it.root
            })
            .setPositiveButton("Добавить") { _, _ ->
                addFriendViewModel.onAddFriend(binding.etNickname.text.toString())
                addFriendViewModel.message.observe(this) {
                    positiveCallback.invoke(it)
                }
            }
            .setNegativeButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            positive: (String) -> Unit
        ) {
            AddFriendDialog().apply {
                positiveCallback = positive
                show(fragmentManager, AddFriendDialog::class.java.name)
            }
        }
    }
}
