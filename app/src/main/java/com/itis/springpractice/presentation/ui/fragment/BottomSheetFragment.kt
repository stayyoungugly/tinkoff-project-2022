package com.itis.springpractice.presentation.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.itis.springpractice.R
import com.itis.springpractice.databinding.FragmentBottomDialogBinding

class BottomSheetFragment : BottomSheetDialogFragment() {

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
        val density = requireContext().resources.displayMetrics.density
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

    fun changeStateToHidden() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun changeStateToExpanded() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }
}
