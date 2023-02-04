package com.itis.springpractice.presentation.ui.diffutil

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.itis.springpractice.domain.entity.Place

class PlaceDiffUtilCallback : DiffUtil.ItemCallback<Place>() {
    override fun areItemsTheSame(oldItem: Place, newItem: Place) =
        oldItem.uri == newItem.uri

    override fun areContentsTheSame(oldItem: Place, newItem: Place) =
        oldItem.address == newItem.address

    override fun getChangePayload(oldItem: Place, newItem: Place): Any? {
        val bundle = Bundle()
        if (oldItem.name != newItem.name) {
            bundle.putString("NAME", newItem.name)
        }
        if (oldItem.photoUrl != newItem.photoUrl) {
            bundle.putString("URL", newItem.name)
        }
        if (oldItem.address != newItem.address) {
            bundle.putString("ADDRESS", newItem.address)
        }
        if (bundle.isEmpty) return null
        return bundle
    }
}
