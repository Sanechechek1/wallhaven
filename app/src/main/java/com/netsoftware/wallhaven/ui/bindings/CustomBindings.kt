package com.netsoftware.wallhaven.ui.bindings

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.data.models.THUMB_ORIGINAL

object CustomBindings {
    @JvmStatic
    @BindingAdapter("imageThumbs")
    fun bindImageView(imageView: ImageView, imageThumbs: MutableMap<String, String>) {
        val thumbSize = THUMB_ORIGINAL
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
}