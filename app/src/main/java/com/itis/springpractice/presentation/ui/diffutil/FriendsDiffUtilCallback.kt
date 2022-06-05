package com.itis.springpractice.presentation.ui.diffutil

import android.os.Bundle
import androidx.recyclerview.widget.DiffUtil
import com.itis.springpractice.domain.entity.User

class FriendsDiffUtilCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User) =
        oldItem.nickname == newItem.nickname

    override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem

    override fun getChangePayload(oldItem: User, newItem: User): Any? {
        val bundle = Bundle()
        if (oldItem.firstName != newItem.firstName) {
            bundle.putString("FIRST_NAME", newItem.firstName)
        }
        if (oldItem.lastName != newItem.lastName) {
            bundle.putString("LAST_NAME", newItem.lastName)
        }
        if (oldItem.nickname != newItem.nickname) {
            bundle.putString("NICKNAME", newItem.nickname)
        }
        if (bundle.isEmpty) return null
        return bundle
    }
}
