package com.itis.springpractice.presentation.ui.rv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.itis.springpractice.databinding.ItemPlaceBinding
import com.itis.springpractice.domain.entity.Place

class PlacesHolder(
    private val binding: ItemPlaceBinding,
    private val glide: RequestManager,
    private val selectItem: (Place) -> Unit,
    private val deleteItem: (String) -> Unit,
    private val isUser: Boolean

) : RecyclerView.ViewHolder(binding.root) {
    private var place: Place? = null

    init {
        itemView.setOnClickListener {
            place?.let { place -> selectItem(place) }
        }
    }


    private val glideOptions by lazy {
        RequestOptions()
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
    }

    fun bind(item: Place) {
        place = item
        with(binding) {
            if (!isUser) {
                btnDelete.visibility = View.GONE
            } else {
                btnDelete.setOnClickListener {
                    deleteItem(item.uri)
                }
            }
            if (item.photoUrl != null) {
                glide.load(item.photoUrl)
                    .apply(glideOptions)
                    .into(binding.ivPhoto)
            }
            tvName.text = item.name
            tvAddress.text = item.address
        }
    }

    fun updateFields(bundle: Bundle) {
        bundle.run {
            getString("NAME")?.also {
                updateName(it)
            }
            getString("ADDRESS")?.also {
                updateAddress(it)
            }
            getString("URL").also {
                if (it != null) {
                    updateUrl(it)
                }
            }
        }
    }

    private fun updateName(name: String) {
        binding.tvName.text = name
    }

    private fun updateAddress(address: String) {
        binding.tvAddress.text = address

    }

    private fun updateUrl(url: String) {
        glide.load(url)
            .apply(glideOptions)
            .into(binding.ivPhoto)
    }

    companion object {
        fun create(
            parent: ViewGroup,
            glide: RequestManager,
            selectItem: (Place) -> Unit,
            deleteItem: (String) -> Unit,
            isUser: Boolean
        ) = PlacesHolder(
            ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            glide,
            selectItem,
            deleteItem,
            isUser
        )
    }
}
