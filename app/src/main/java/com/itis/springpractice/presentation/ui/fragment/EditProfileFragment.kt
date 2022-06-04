package com.itis.springpractice.presentation.ui.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import java.io.ByteArrayOutputStream

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
            editProfileViewModel.onUpdateUser(firstName, lastName, getAvatar())
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
        val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { localUri ->
            binding.ivPhoto.setImageURI(localUri)
        }
//        binding.ivPhoto.setOnClickListener {
//            getContent.launch("image/*")
//        }
    }

    private fun getAvatar(): ByteArray {
        val bitmap = (binding.ivPhoto.drawable as BitmapDrawable).bitmap
        return ByteArrayOutputStream().run {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
            toByteArray()
        }
    }

    private fun initObservers() {
        editProfileViewModel.error.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                findNavController().navigate(R.id.action_editProfileFragment_to_authorizedFragment)
            } else showMessage(it)
        }

        editProfileViewModel.user.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                binding.etFirstName.setText(it.firstName)
                binding.etLastName.setText(it.lastName)
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
}
