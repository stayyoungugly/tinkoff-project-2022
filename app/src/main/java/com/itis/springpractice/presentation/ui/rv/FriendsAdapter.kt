package com.itis.springpractice.presentation.ui.rv

import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.itis.springpractice.domain.entity.User
import com.itis.springpractice.presentation.ui.diffutil.FriendsDiffUtilCallback

class FriendsAdapter(
    private val selectItem: (String) -> Unit
) : ListAdapter<User, FriendsHolder>(FriendsDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsHolder =
        FriendsHolder.create(parent, selectItem)

    override fun onBindViewHolder(holder: FriendsHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: FriendsHolder,
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

    override fun submitList(list: MutableList<User>?) {
        super.submitList(if (list == null) null else ArrayList(list))
    }
}
