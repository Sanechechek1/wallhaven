package com.netsoftware.wallhaven.ui.adapters

import android.view.View
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.databinding.RvItemViewerBinding
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder


class WallpaperItem(private val wallpaper: Wallpaper) : AbstractFlexibleItem<WallpaperItem.WallpapersViewHolder>() {

    override fun equals(other: Any?): Boolean {
        return if (other is Wallpaper) {
            wallpaper == other
        } else false
    }

    override fun hashCode(): Int {
        return wallpaper.hashCode()
    }

    override fun getLayoutRes(): Int = R.layout.rv_item_viewer

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): WallpapersViewHolder =
        WallpapersViewHolder(RvItemViewerBinding.bind(view), adapter)

    override fun bindViewHolder(
        adapter: FlexibleAdapter<IFlexible<*>>,
        holder: WallpapersViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        holder.bind(position)
    }


    inner class WallpapersViewHolder(private val binding: RvItemViewerBinding, adapter: FlexibleAdapter<*>) :
        FlexibleViewHolder(binding.root, adapter) {

        fun bind(position: Int) {
            binding.wallpaper = this@WallpaperItem.wallpaper
//            viewModel.fetchDogBreedImagesAt(position)
//            binding.setVariable(BR.viewModel, viewModel)
//            binding.setVariable(BR.position, position)
            binding.executePendingBindings()
        }

    }
}