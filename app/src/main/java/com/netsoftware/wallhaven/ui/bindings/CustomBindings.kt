package com.netsoftware.wallhaven.ui.bindings

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.data.dataSources.local.SharedPrefs
import com.netsoftware.wallhaven.data.models.Tag
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.utility.extensions.GlideApp

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
    @BindingAdapter("chips", "darkBackground", "chipClick", requireAll = false)
    fun bindStringChips(
        chipGroup: ChipGroup,
        chips: Array<String>,
        darkBackground: Boolean,
        onCheckedChangeListener: CompoundButton.OnCheckedChangeListener?
    ) {
        val itsColors = chips[0][0] == '#'
        for (chipText in chips) {
            val chip = Chip(chipGroup.context)
            chip.setChipDrawable(
                ChipDrawable.createFromAttributes(
                    chipGroup.context, null, 0,
                    if (darkBackground) R.style.Base_Base_Widget_MaterialComponents_Chip_MyChip_Dark
                    else R.style.Base_Base_Widget_MaterialComponents_Chip_MyChip_Light
                )
            )
            if (itsColors) {
                chip.chipBackgroundColor = ColorStateList(
                    arrayOf(
                        intArrayOf(android.R.attr.state_enabled)
                    ),
                    intArrayOf(Color.parseColor(chipText))
                )
                chip.isCheckedIconVisible = true
                chip.chipStartPadding = chip.chipMinHeight / 2
                chip.chipEndPadding = chip.chipMinHeight / 2
                chip.textEndPadding = 0F
                chip.textStartPadding = 0F
                chip.closeIconEndPadding = 0F
                chip.closeIconStartPadding = 0F
                chip.iconStartPadding = 0F
                chip.iconEndPadding = 0F
            } else chip.text = chipText
            chip.setOnCheckedChangeListener(onCheckedChangeListener)
            chipGroup.addView(chip)
        }
    }

    @JvmStatic
    @BindingAdapter("chipClick", requireAll = false)
    fun bindChip(
        chip: Chip,
        onCheckedChangeListener: CompoundButton.OnCheckedChangeListener?
    ) {
        chip.setChipDrawable(
            ChipDrawable.createFromAttributes(
                chip.context, null, 0,
                R.style.Base_Base_Widget_MaterialComponents_Chip_MyChip_Dark
            )
        )
        chip.text = SharedPrefs.getSharedPrefs().screenResolution
        chip.setOnCheckedChangeListener(onCheckedChangeListener)
    }
}