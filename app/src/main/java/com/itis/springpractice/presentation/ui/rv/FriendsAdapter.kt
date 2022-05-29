package com.itis.springpractice.presentation.ui.rv

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.itis.springpractice.domain.entity.User

class FriendsAdapter(
    private val list: List<User>,
    private val selectItem: (String) -> Unit
) : RecyclerView.Adapter<FriendsHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsHolder =
        FriendsHolder.create(parent, selectItem)

    override fun onBindViewHolder(holder: FriendsHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}
