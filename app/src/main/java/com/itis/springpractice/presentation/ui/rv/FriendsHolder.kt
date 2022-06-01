package com.itis.springpractice.presentation.ui.rv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itis.springpractice.databinding.ItemFriendBinding
import com.itis.springpractice.domain.entity.User

class FriendsHolder(
    private val binding: ItemFriendBinding,
    private val selectItem: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    private var friend: User? = null

    init {
        itemView.setOnClickListener {
            friend?.nickname?.also(selectItem)
        }
    }

    fun bind(item: User) {
        friend = item
        with(binding) {
            tvNickname.text = item.nickname
            tvFullName.text = "${item.firstName} ${item.lastName}"
        }
    }

    fun updateFields(bundle: Bundle) {
        bundle.run {
            getString("NICKNAME")?.also {
                binding.tvNickname.text = it
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup, selectItem: (String) -> Unit) = FriendsHolder(
            ItemFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            selectItem
        )
    }
}
