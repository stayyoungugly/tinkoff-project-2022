package com.itis.springpractice.presentation.ui.rv

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.itis.springpractice.R
import com.itis.springpractice.databinding.ItemReviewBinding
import com.itis.springpractice.domain.entity.Place
import com.itis.springpractice.domain.entity.Review

class ReviewsHolder(
    private val binding: ItemReviewBinding,
    private val glide: RequestManager,
    private val selectItem: ((Place) -> Unit)?,
    private val deleteItem: (String) -> Unit,
    private val nickname: String,
) : RecyclerView.ViewHolder(binding.root) {
    private var review: Review? = null

    init {
        itemView.setOnClickListener {
            review?.place?.let { place -> selectItem?.let { select -> select(place) } }
        }
    }

    private val glideOptions by lazy {
        RequestOptions()
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    fun bind(item: Review) {
        review = item
        with(binding) {
            item.place?.let { it ->
                tvNickname.text = it.category
                tvFullName.text = it.name
                if (it.photoUrl != null) {
                    glide.load(it.photoUrl)
                        .apply(glideOptions)
                        .into(binding.ivAvatar)
                } else ivAvatar.setImageResource(R.drawable.no_photo)

            }
            if (item.place == null) {
                tvNickname.text = item.author.nickname
                tvFullName.text = "${item.author.firstName} ${item.author.lastName}"
            }

            tvContent.text = item.textReview
            tvDate.text = item.created
            rbRating.rating = item.rating

            if (item.author.avatar != null) {
                val bitmap =
                    BitmapFactory.decodeByteArray(item.author.avatar, 0, item.author.avatar.size)
                this.ivAvatar.setImageBitmap(bitmap)
            }
            val size = nickname.length
            if (item.author.nickname.take(size) == nickname) {
                btnDelete.visibility = View.VISIBLE
                btnDelete.setOnClickListener {
                    deleteItem(item.uri)
                }
            }
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
        binding.tvFullName.text = "$firstname $lastname"
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
        fun create(
            parent: ViewGroup,
            glide: RequestManager,
            selectItem: ((Place) -> Unit)?,
            deleteItem: (String) -> Unit,
            nickname: String
        ) = ReviewsHolder(
            ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            glide,
            selectItem,
            deleteItem,
            nickname
        )
    }
}
