package com.netsoftware.wallhaven.ui.bindings

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.netsoftware.wallhaven.data.models.THUMB_SMALL

object CustomBindings{
    @JvmStatic
    @BindingAdapter("imageThumbs")
    fun bindImageView(imageView: ImageView, imageThumbs: MutableMap<String, String>) {
        if (imageThumbs.isNotEmpty()) {
            imageView.clipToOutline = true
            // If we don't do this, you'll see the old image appear briefly
            // before it's replaced with the current image
            GlideApp.with(imageView).load(imageThumbs[THUMB_SMALL]).into(imageView)
//            if (imageView.getTag(R.id.image_url) == null || imageView.getTag(R.id.image_url) != imageUrl) {
//                imageView.setImageBitmap(null)
//                imageView.setTag(R.id.image_url, imageUrl)
//                GlideApp.with(imageView).load(imageUrl).into(imageView)
//            }
        } else {
//            imageView.setTag(R.id.image_url, null)
//            imageView.setImageBitmap(null)
        }
    }
}