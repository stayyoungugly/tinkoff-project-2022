package com.itis.springpractice.presentation.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentCollectionsBinding

class CollectionsFragment : Fragment(R.layout.fragment_collections) {
    private lateinit var binding: FragmentCollectionsBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCollectionsBinding.bind(view)
    }
}
