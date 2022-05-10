package com.itis.springpractice.presentation.ui.fragment.extension

import androidx.fragment.app.Fragment

inline fun <reified T : Any> Fragment.findParent(): T? {
    var currentFragment: Fragment = this
    while (currentFragment !is T) {
        if (currentFragment.parentFragment == null) return currentFragment.requireActivity() as? T
        currentFragment = currentFragment.requireParentFragment()
    }
    return currentFragment
}
