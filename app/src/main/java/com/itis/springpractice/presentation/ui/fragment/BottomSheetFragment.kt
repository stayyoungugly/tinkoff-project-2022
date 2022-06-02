package com.itis.springpractice.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentBottomDialogBinding
import com.itis.springpractice.presentation.ui.fragment.utils.ParentFragmentPagerAdapter

class BottomSheetFragment(uri: String) : BottomSheetDialogFragment() {

    private val uriPlace = uri

    private lateinit var binding: FragmentBottomDialogBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun getTheme() = R.style.AppBottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomDialogBinding.bind(
            inflater.inflate(
                R.layout.fragment_bottom_dialog,
                container
            )
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setViewPager()
        dialog?.let {
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
                state = BottomSheetBehavior.STATE_EXPANDED
                addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            state = BottomSheetBehavior.STATE_HIDDEN
                        }
                    }
                })
            }
        }
    }

    private fun setViewPager() {
        val viewPager: ViewPager2 = binding.viewPager
        val tabLayout: TabLayout = binding.tabLayout

        val pagerAdapter = ParentFragmentPagerAdapter(this, uriPlace)
        viewPager.adapter = pagerAdapter
        viewPager.isSaveEnabled = false

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val tabNames = listOf("Инфо", "Отзывы")
            tab.text = tabNames[position]
        }.attach()
    }
}
