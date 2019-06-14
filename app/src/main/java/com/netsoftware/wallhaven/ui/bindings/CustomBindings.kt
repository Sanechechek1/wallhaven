package com.netsoftware.wallhaven.ui.bindings

import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.data.dataSources.local.SharedPrefs
import com.netsoftware.wallhaven.data.models.Tag
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.utility.extensions.GlideApp
import com.netsoftware.wallhaven.utility.extensions.dpToPx

object CustomBindings {
    @JvmStatic
    @BindingAdapter("imageThumbs")
    fun bindImageView(imageView: ImageView, imageThumbs: MutableMap<String, String>) {
        val thumbSize = SharedPrefs.getSharedPrefs().thumbSize
        if (imageThumbs.isNotEmpty()) {
            imageView.clipToOutline = true
            // If we don't do this, you'll see the old image appear briefly
            // before it's replaced with the current image
            if (imageView.getTag(R.id.image_url) == null || imageView.getTag(R.id.image_url) != imageThumbs[thumbSize]) {
                imageView.setImageBitmap(null)
                imageView.setTag(R.id.image_url, imageThumbs[thumbSize])
                GlideApp.with(imageView)
                    .load(imageThumbs[thumbSize])
                    .centerInside()
                    .into(imageView)
            }
        } else {
            imageView.setTag(R.id.image_url, null)
            imageView.setImageBitmap(null)
        }
    }

    @JvmStatic
    @BindingAdapter("detailText")
    fun bindDetailText(textView: TextView, wallpaper: Wallpaper) {
        when (textView.id) {
            R.id.detail_resolution -> textView.text =
                textView.resources.getString(R.string.title_detail_resolution).format(wallpaper.resolution)
            R.id.detail_size -> textView.text =
                textView.resources.getString(R.string.title_detail_size).format(wallpaper.fileSize)
            R.id.detail_category -> textView.text =
                textView.resources.getString(R.string.title_detail_category).format(wallpaper.category)
            R.id.detail_purity -> textView.text =
                textView.resources.getString(R.string.title_detail_purity).format(wallpaper.purity)
        }
    }

    @JvmStatic
    @BindingAdapter("chips", "chipClick")
    fun bindTagsChips(chipGroup: ChipGroup, tags: List<Tag>, onClickListener: View.OnClickListener) {
        for (tag in tags) {
            val chip = Chip(chipGroup.context)
            chip.text = tag.name
            chip.tag = tag.id
            chip.isClickable = true
            chip.setOnClickListener(onClickListener)
            chipGroup.addView(chip)
        }
    }

    @JvmStatic
    @BindingAdapter("chips", "chipClick")
    fun bindStringChips(
        chipGroup: ChipGroup,
        resolutions: Array<String>,
        onCheckedChangeListener: CompoundButton.OnCheckedChangeListener
    ) {
        for (resolution in resolutions) {
            val chip = Chip(chipGroup.context)
            chip.text = resolution
            chip.textStartPadding = 8.dpToPx.toFloat()
            chip.textEndPadding = 8.dpToPx.toFloat()
            chip.isCheckedIconVisible = false
            chip.chipBackgroundColor = chipGroup.context.getColorStateList(R.color.chip_background)
            chip.rippleColor = chipGroup.context.getColorStateList(R.color.chip_background)
            chip.isClickable = true
            chip.isCheckable = true
            chip.setOnCheckedChangeListener(onCheckedChangeListener)
            chipGroup.addView(chip)
        }
    }
}