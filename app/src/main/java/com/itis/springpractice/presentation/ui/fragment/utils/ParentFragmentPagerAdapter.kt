package com.itis.springpractice.presentation.ui.fragment.utils

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.itis.springpractice.presentation.ui.fragment.PlaceInfoFragment
import com.itis.springpractice.presentation.ui.fragment.PlaceReviewFragment

class ParentFragmentPagerAdapter(
    fragment: Fragment,
    uri: String
) : FragmentStateAdapter(fragment) {

    private val uriPlace = uri

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PlaceInfoFragment(uriPlace)
            1 -> PlaceReviewFragment(uriPlace)
            else -> PlaceInfoFragment(uriPlace)
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}
