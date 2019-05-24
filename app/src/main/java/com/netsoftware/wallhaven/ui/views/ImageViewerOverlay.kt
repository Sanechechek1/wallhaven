package com.netsoftware.wallhaven.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.databinding.ViewerImageOverlayBinding

class ImageViewerOverlay : ConstraintLayout {
    var binding: ViewerImageOverlayBinding =
        ViewerImageOverlayBinding.inflate(LayoutInflater.from(context), this, true)
    lateinit var wallpaper: Wallpaper

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }

    constructor(context: Context?) : super(context)

    constructor(
        context: Context?,
        wallpaper: Wallpaper,
        chipClickListener: OnClickListener? = null,
        isLoading: MutableLiveData<Boolean> = MutableLiveData(false),
        lifecycleOwner: LifecycleOwner? = null
    ) : super(context) {
        this.wallpaper = wallpaper
        binding.wall = wallpaper
        binding.chipClick = chipClickListener
        binding.isLoading = isLoading
        binding.lifecycleOwner = lifecycleOwner
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet)

    constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )
}