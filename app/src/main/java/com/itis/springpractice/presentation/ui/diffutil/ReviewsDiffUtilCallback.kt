package com.itis.springpractice.presentation.ui.diffutil

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.itis.springpractice.domain.entity.Review

class ReviewsDiffUtilCallback : DiffUtil.ItemCallback<Review>() {
    override fun areItemsTheSame(oldItem: Review, newItem: Review) =
        oldItem.author == newItem.author

    override fun areContentsTheSame(oldItem: Review, newItem: Review) =
        oldItem.author == newItem.author

    override fun getChangePayload(oldItem: Review, newItem: Review): Any? {
        val bundle = Bundle()
        if (oldItem.author.nickname != newItem.author.nickname) {
            bundle.putString("AUTHOR_NICKNAME", newItem.author.nickname)
        }
        if (oldItem.author.firstName != newItem.author.firstName) {
            bundle.putString("AUTHOR_FIRSTNAME", newItem.author.firstName)
        }
        if (oldItem.author.lastName != newItem.author.lastName) {
            bundle.putString("AUTHOR_LASTNAME", newItem.author.lastName)
        }
        if (oldItem.created != newItem.created) {
            bundle.putString("CREATED", newItem.created)
        }
        if (oldItem.rating != newItem.rating) {
            bundle.putFloat("RATING", newItem.rating)
        }
        if (oldItem.textReview != newItem.textReview) {
            bundle.putString("CONTENT", newItem.textReview)
        }
        if (bundle.isEmpty) return null
        return bundle
    }
}
