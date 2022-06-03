package com.itis.springpractice.presentation.ui.rv

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.itis.springpractice.domain.entity.Review
import com.itis.springpractice.presentation.ui.diffutil.ReviewsDiffUtilCallback

class ReviewsAdapter(
    private val selectItem: (String) -> Unit
) : ListAdapter<Review, ReviewsHolder>(ReviewsDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsHolder =
        ReviewsHolder.create(parent, selectItem)

    override fun onBindViewHolder(holder: ReviewsHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: ReviewsHolder,
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

    override fun submitList(list: MutableList<Review>?) {
        super.submitList(if (list == null) null else ArrayList(list))
    }
}
