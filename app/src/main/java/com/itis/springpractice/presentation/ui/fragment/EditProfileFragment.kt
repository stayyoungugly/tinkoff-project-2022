package com.itis.springpractice.presentation.ui.fragment

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentEditProfileBinding
import com.itis.springpractice.presentation.viewmodel.EditProfileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private val binding by viewBinding(FragmentEditProfileBinding::bind)

    private val editProfileViewModel: EditProfileViewModel by viewModel()

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
        val getContent =
            registerForActivityResult(ActivityResultContracts.GetContent()) { localUri ->
                binding.ivPhoto.setImageURI(localUri)
            }
        binding.ivPhoto.setOnClickListener {
            getContent.launch("image/*")
        }
    }

    private lateinit var oldAvatar: Bitmap

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
                setAvatar(it.avatar)
            }, onFailure = {
                showMessage("Проверьте подключение к интернету")
            })
        }
    }

    private fun setAvatar(avatar: ByteArray?) {
        oldAvatar = if (avatar == null) {
            val bitmap =
                BitmapFactory.decodeResource(requireContext().resources, R.drawable.no_avatar)
            binding.ivPhoto.setImageBitmap(bitmap)
            bitmap
        } else {
            val bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.size)
            binding.ivPhoto.setImageBitmap(bitmap)
            bitmap
        }
    }

    private fun getAvatar(): ByteArray {
        val bitmap = if (binding.ivPhoto.drawable == null) {
            oldAvatar
        } else {
            (binding.ivPhoto.drawable as BitmapDrawable).bitmap
        }
        return ByteArrayOutputStream().run {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
            toByteArray()
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
