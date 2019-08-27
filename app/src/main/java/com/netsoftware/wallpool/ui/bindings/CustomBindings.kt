package com.netsoftware.wallpool.ui.bindings

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
import com.netsoftware.wallpool.R
import com.netsoftware.wallpool.data.dataSources.local.SharedPrefs
import com.netsoftware.wallpool.data.models.Tag
import com.netsoftware.wallpool.data.models.Wallpaper
import com.netsoftware.wallpool.ui.main.ViewerViewModel
import com.netsoftware.wallpool.utility.extensions.GlideApp
import com.netsoftware.wallpool.utility.extensions.sizeToReadable

object CustomBindings {
    @JvmStatic
    @BindingAdapter("imageThumbs", "viewerType", requireAll = false)
    fun bindImageView(
        imageView: ImageView,
        imageThumbs: MutableMap<String, String>,
        viewerType: ViewerViewModel.ViewerType
    ) {
        val thumbSize = SharedPrefs.getSharedPrefs().thumbSize
        if (imageThumbs.isNotEmpty()) {
            imageView.clipToOutline = true
            // If we don't do this, you'll see the old image appear briefly
            // before it's replaced with the current image
            if (imageView.getTag(R.id.image_url) == null || imageView.getTag(R.id.image_url) != imageThumbs[thumbSize]) {
                imageView.setImageBitmap(null)
                imageView.setTag(R.id.image_url, imageThumbs[thumbSize])
                GlideApp.with(imageView).load(imageThumbs[thumbSize]).centerInside().into(imageView)
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
                textView.resources.getString(R.string.title_detail_size).format(wallpaper.fileSize.sizeToReadable())
            R.id.detail_category -> textView.text =
                textView.resources.getString(R.string.title_detail_category).format(wallpaper.category.toUpperCase())
            R.id.detail_purity -> textView.text =
                textView.resources.getString(R.string.title_detail_purity).format(wallpaper.purity.toUpperCase())
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
        val myRatio = SharedPrefs.getSharedPrefs().screenRatio
        val itsColors = chips[0][0] == '#'
        val createChip = fun(): Chip {
            return Chip(chipGroup.context).apply {
                setChipDrawable(
                    ChipDrawable.createFromAttributes(
                        chipGroup.context, null, 0,
                        if (darkBackground) R.style.Base_Base_Widget_MaterialComponents_Chip_MyChip_Dark
                        else R.style.Base_Base_Widget_MaterialComponents_Chip_MyChip_Light
                    )
                )
                setOnCheckedChangeListener(onCheckedChangeListener)
            }
        }
        if (!itsColors && !chips.contains(myRatio) && chips.first().length<7)
            chipGroup.addView(createChip().apply {
                text = chipGroup.context.getString(R.string.title_my, myRatio)
            })
        for (chipText in chips) {
            val chip = createChip()
            when {
                itsColors -> chip.apply {
                    chipBackgroundColor = ColorStateList(
                        arrayOf(
                            intArrayOf(android.R.attr.state_enabled)
                        ),
                        intArrayOf(Color.parseColor(chipText))
                    )
                    tag = chipText
                    isCheckedIconVisible = true
                    chipStartPadding = chip.chipMinHeight / 2
                    chipEndPadding = chip.chipMinHeight / 2
                    textEndPadding = 0F
                    textStartPadding = 0F
                    closeIconEndPadding = 0F
                    closeIconStartPadding = 0F
                    iconStartPadding = 0F
                    iconEndPadding = 0F
                }
                chipText == myRatio -> chip.text = chipGroup.context.getString(R.string.title_my, chipText)
                else -> chip.text = chipText
            }
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