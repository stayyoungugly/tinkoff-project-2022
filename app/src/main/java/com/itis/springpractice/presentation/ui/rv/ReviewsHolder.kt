package com.itis.springpractice.presentation.ui.rv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itis.springpractice.databinding.ItemReviewBinding
import com.itis.springpractice.domain.entity.Review

class ReviewsHolder(
    private val binding: ItemReviewBinding,
    private val selectItem: (String) -> Unit,

    ) : RecyclerView.ViewHolder(binding.root) {
    private var review: Review? = null

    init {
        itemView.setOnClickListener {
            review?.author?.nickname?.also(selectItem)
        }
    }

    fun bind(item: Review) {
        review = item
        with(binding) {
            tvNickname.text = item.author.nickname
            tvFullName.text = "${item.author.firstName} ${item.author.lastName}"
            tvContent.text = item.textReview
            tvDate.text = item.created
            rbRating.rating = item.rating
        }
    }

    fun updateFields(bundle: Bundle) {
        bundle.run {
            getString("CONTENT")?.also {
                updateContent(it)
            }
            getString("CREATED")?.also {
                updateCreated(it)
            }
            getFloat("RATING").also {
                updateRating(it)
            }
            getString("AUTHOR_NICKNAME")?.also {
                updateNickname(it)
            }
            getString("AUTHOR_FIRSTNAME")?.also { firstname ->
                getString("AUTHOR_LASTNAME")?.also {
                    updateFullname(firstname, it)
                }
            }
        }
    }

    private fun updateFullname(firstname: String, lastname: String) {
        binding.tvFullName.text = "${firstname} ${lastname}"
    }

    private fun updateNickname(nickname: String) {
        binding.tvNickname.text = nickname

    }

    private fun updateContent(content: String) {
        binding.tvContent.text = content
    }

    private fun updateCreated(date: String) {
        binding.tvDate.text = date
    }

    private fun updateRating(rating: Float) {
        binding.rbRating.rating = rating
    }

    companion object {
        fun create(parent: ViewGroup, selectItem: (String) -> Unit) = ReviewsHolder(
            ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            selectItem
        )
    }
}
