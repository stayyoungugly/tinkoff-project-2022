package com.itis.springpractice.presentation.ui.rv

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.RequestManager
import com.itis.springpractice.domain.entity.Place
import com.itis.springpractice.presentation.ui.diffutil.PlaceDiffUtilCallback

class PlacesAdapter(
    private val glide: RequestManager,
    private val selectItem: (Place) -> Unit,
    private val deleteItem: (String) -> Unit,
    private val isUser: Boolean
) : ListAdapter<Place, PlacesHolder>(PlaceDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesHolder =
        PlacesHolder.create(parent, glide, selectItem, deleteItem, isUser)

    override fun onBindViewHolder(holder: PlacesHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: PlacesHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.last().takeIf { it is Bundle }?.let {
                holder.updateFields(it as Bundle)
            }
        }
    }

    override fun submitList(list: MutableList<Place>?) {
        super.submitList(if (list == null) null else ArrayList(list))
    }
}
