package com.itis.springpractice.presentation.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentEditProfileBinding
import com.itis.springpractice.di.FriendContainer
import com.itis.springpractice.di.UserAuthContainer
import com.itis.springpractice.di.UserContainer
import com.itis.springpractice.di.UserTokenContainer
import com.itis.springpractice.presentation.factory.AuthFactory
import com.itis.springpractice.presentation.viewmodel.EditProfileViewModel

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private val binding by viewBinding(FragmentEditProfileBinding::bind)

    private val editProfileViewModel by viewModels<EditProfileViewModel> {
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
        editProfileViewModel.onGetUserInfo()
        binding.btnAccept.setOnClickListener {
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val nickname = binding.etNickname.text.toString()
            //TODO("get image")
            editProfileViewModel.onUpdateUser(firstName, lastName, nickname)
            findNavController().navigate(R.id.action_editProfileFragment_to_authorizedFragment)
        }
        binding.btnCancel.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Отмена")
                .setMessage("Отменить изменения?")
                .setPositiveButton("ОК") { _, _ ->
                    findNavController().navigate(R.id.action_editProfileFragment_to_authorizedFragment)
                }
                .setNegativeButton("Отмена") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
        binding.ivPhoto.setOnClickListener {
            val imageIntent = Intent().apply {
                action = Intent.ACTION_PICK
                type = "image/*"
            }
            if (imageIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(imageIntent)
            }
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                binding.ivPhoto.setImageURI(uri)
                //val file = File(uri.path)
                //TODO("upload image to storage")
            }.apply {
                launch("image/*")
            }
        }
    }

    private fun initObservers() {
        editProfileViewModel.user.observe(viewLifecycleOwner) {
            it.fold(onSuccess = {
                with(binding) {
                    this.etFirstName.setText(it.firstName)
                    this.etLastName.setText(it.lastName)
                    this.etNickname.setText(it.nickname)
                    //TODO("set image")
                }
            }, onFailure = {
                Snackbar.make(
                    binding.root,
                    "Проверьте подключение к интернету",
                    Snackbar.LENGTH_LONG
                ).show()
            })
        }
//        editProfileViewModel.message.observe(viewLifecycleOwner) {
//            if (it.isEmpty()) {
//                findNavController().navigate(R.id.action_editProfileFragment_to_authorizedFragment)
//            } else {
//                binding.etNickname.error = it
//            }
//        }
    }
}
