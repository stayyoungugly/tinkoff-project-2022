package com.itis.springpractice.presentation.ui.fragment

import android.graphics.drawable.Icon
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentBottomDialogBinding
import com.itis.springpractice.domain.entity.Place
import com.itis.springpractice.presentation.ui.fragment.utils.ParentFragmentPagerAdapter
import com.itis.springpractice.presentation.viewmodel.PlaceInfoViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber


class BottomSheetFragment(uri: String) : BottomSheetDialogFragment() {
    private val uriPlace = uri

    private lateinit var binding: FragmentBottomDialogBinding

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private var isUp = false

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    private val placeInfoViewModel: PlaceInfoViewModel by viewModel()

    private var liked = false

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        placeInfoViewModel.searchGeoObjectInfo(uriPlace)
        binding.btnDown.setOnClickListener {
            onButtonClick()
        }
    }

    private fun setMenuButtons() {
        with(binding) {
            btnCancel.setOnClickListener {
                dialog?.dismiss()
            }
            btnAdd.setOnClickListener {}
            btnLike.setOnClickListener {
                liked = if (liked) {
                    btnLike.setImageIcon(Icon.createWithResource(context, R.drawable.ic_not_liked))
                    placeInfoViewModel.deleteLike(uriPlace)
                    false
                } else {
                    btnLike.setImageIcon(Icon.createWithResource(context, R.drawable.ic_liked))
                    placeInfoViewModel.addLike(uriPlace)
                    true
                }
            }
        }
    }

    private fun initObservers() {
        placeInfoViewModel.place.observe(viewLifecycleOwner) { result ->
            result.fold(onSuccess = {
                setPlaceInfo(it)
            }, onFailure = {
                showMessage(getString(R.string.try_again))
            })
        }

        placeInfoViewModel.isPlaceLiked.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.btnLike.setImageIcon(Icon.createWithResource(context, R.drawable.ic_liked))
                liked = true
            }
        }

        placeInfoViewModel.error.observe(viewLifecycleOwner) {
            Timber.e(it)
            showMessage(getString(R.string.info_not_found))
        }
    }

    private fun setPlaceInfo(place: Place) {
        placeInfoViewModel.isPlaceLiked(uriPlace)
        with(binding) {
            tvCategory.text = place.category
            tvName.text = place.name
        }
        setMenuButtons()
    }

    private fun showMessage(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }


    private fun onButtonClick() {
        if (isUp) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.btnDown.setImageIcon(Icon.createWithResource(context, R.drawable.ic_arrow_up))
            isUp = false
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            binding.btnDown.setImageIcon(Icon.createWithResource(context, R.drawable.ic_arrow_down))
            isUp = true
        }
    }

    override fun onStart() {
        super.onStart()
        setViewPager()
        dialog?.let {
            val bottomSheet =
                it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
                state = BottomSheetBehavior.STATE_COLLAPSED
                it.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                val dm = resources.displayMetrics
                peekHeight = (dm.density * 110).toInt()

                addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}

                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            it.dismiss()
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
