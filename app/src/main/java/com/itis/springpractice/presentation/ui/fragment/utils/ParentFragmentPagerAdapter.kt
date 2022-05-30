package com.itis.springpractice.presentation.ui.fragment.utils

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.itis.springpractice.presentation.ui.fragment.PlaceInfoFragment
import com.itis.springpractice.presentation.ui.fragment.PlaceReviewFragment

class ParentFragmentPagerAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PlaceInfoFragment()
            1 -> PlaceReviewFragment()
            else -> PlaceInfoFragment()
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}
