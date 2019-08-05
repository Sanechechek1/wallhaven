package com.netsoftware.wallpool.ui.adapters

import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import com.netsoftware.wallpool.R
import com.netsoftware.wallpool.data.models.Wallpaper
import com.netsoftware.wallpool.databinding.RvItemViewerBinding
import com.netsoftware.wallpool.ui.main.ViewerViewModel
import com.netsoftware.wallpool.utility.managers.MyDisplayManager
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder

class WallpaperItem(private var wallpaper: Wallpaper, private val type: ViewerViewModel.ViewerType) :
    AbstractFlexibleItem<WallpaperItem.WallpapersViewHolder>() {

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
        holder.bind(wallpaper)
    }

    inner class WallpapersViewHolder(val binding: RvItemViewerBinding, adapter: FlexibleAdapter<*>) :
        FlexibleViewHolder(binding.root, adapter) {

        fun bind(wallpaper: Wallpaper) {
            with(binding) {
                val set = ConstraintSet()
                set.clone(container)
                set.setDimensionRatio(
                    ivThumbnail.id,
                    wallpaper.ratio.replace(MyDisplayManager.delimiter, ":")
                )
                set.applyTo(container)
                binding.viewerType = type
                binding.wallpaper = wallpaper
                binding.executePendingBindings()
            }

        }
    }
}

