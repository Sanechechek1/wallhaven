package com.netsoftware.wallhaven.ui.views

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.mikepenz.essentialpack_typeface_library.EssentialPack
import com.mikepenz.essentialpackfilled_typeface_library.EssentialPackFilled
import com.mikepenz.iconics.IconicsDrawable
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.WallhavenApp
import com.netsoftware.wallhaven.data.dataSources.local.SharedPrefs
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.databinding.ViewerImageOverlayBinding
import com.netsoftware.wallhaven.utility.extensions.GlideApp
import java.io.File

class ImageViewerOverlay(
    context: Context?,
    private val lifecycleOwner: LifecycleOwner?,
    private val wallLiveData: MutableLiveData<Wallpaper>?,
    chipClickListener: OnClickListener?
) : ConstraintLayout(context) {
    var binding: ViewerImageOverlayBinding =
        ViewerImageOverlayBinding.inflate(LayoutInflater.from(context), this, true)
    lateinit var container: ImageView
    val pathToSave =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path + File.separator +
                context?.getString(R.string.app_name) + File.separator + "wallhaven_${wallLiveData?.value?.id}.jpg"

    init {
        this.tag =
            setBackgroundColor(Color.TRANSPARENT)
        lifecycleOwner?.let {
            wallLiveData?.observe(it, Observer { wall ->
                binding.wall = wall
            })
        }
        binding.isLoading = MutableLiveData(false)
        binding.chipClick = chipClickListener
        binding.lifecycleOwner = lifecycleOwner
        binding.browserBtn.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(wallLiveData?.value?.postUrl))
            context?.startActivity(browserIntent)
        }
        wallLiveData?.value?.isLiked?.let { activateLike(it) }
        toggleBtns(false)
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : this(context, null, null, null)

    constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, null, null, null)

    private fun activateLike(activated: Boolean) {
        val context = context ?: binding.favoriteBtn.context
        if (activated) {
            binding.favoriteBtn.icon = IconicsDrawable(
                context,
                EssentialPackFilled.Icon.esf_like_2_filled
            ).sizePx(resources.getDimension(R.dimen.iicon_size).toInt())
                .color(context.getColor(R.color.primary_light_text))
        } else {
            binding.favoriteBtn.icon = IconicsDrawable(
                context,
                EssentialPack.Icon.esp_like_2
            ).sizePx(resources.getDimension(R.dimen.iicon_size).toInt())
                .color(context.getColor(R.color.primary_light_text))
        }
        binding.favoriteBtn.isActivated = activated
        wallLiveData?.value?.isLiked = activated
    }

    fun showDetailedWall(container: ImageView) {
        this.container = container
        val listener = object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
            ): Boolean {
                binding.isLoading?.value = false
                binding.noInternetLabel.apply {
                    visibility = View.VISIBLE
                    startAnimation(AnimationUtils.loadAnimation(context, R.anim.extend_bottom))
                }
                return false
            }

            override fun onResourceReady(
                resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                binding.isLoading?.value = false
                toggleBtns(true)
                return false
            }

        }
        wallLiveData?.value?.let {
            binding.isLoading?.value = true
            if (container.id == com.stfalcon.imageviewer.R.id.transitionImageView) {
                GlideApp.with(WallhavenApp.appComponent.getAppContext())
                    .load(it.thumbs[SharedPrefs.getSharedPrefs().thumbSize])
                    .fitCenter()
                    .into(container)
            } else {
                GlideApp.with(WallhavenApp.appComponent.getAppContext()).load(it.url)
                    .thumbnail(
                        GlideApp.with(context)
                            .load(it.thumbs[SharedPrefs.getSharedPrefs().thumbSize]).fitCenter()
                    )
                    .format(DecodeFormat.PREFER_RGB_565)
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .listener(listener)
                    .fitCenter()
                    .into(container)
            }
        }
    }

    fun toggleBtns(enable: Boolean) {
        binding.downloadBtn.isEnabled = enable
        binding.setWallpaperBtn.isEnabled = enable
    }

    fun setFavoriteListener(call: (wall: Wallpaper, isAdded: Boolean) -> Unit) {
        binding.favoriteBtn.setOnClickListener {
            if (it.isActivated) {
                activateLike(false)
                wallLiveData?.value?.let { wall -> call(wall, true) }
            } else {
                activateLike(true)
                wallLiveData?.value?.let { wall -> call(wall, false) }
            }
        }
    }

    fun setSaveImageListener(call: (bitmap: Bitmap, path: String) -> Unit) {
        binding.downloadBtn.setOnClickListener { loadBitmap(call) }
    }

    fun setCropWallpaperListener(call: (path: String) -> Unit) {
        binding.setWallpaperBtn.setOnClickListener {
            call(pathToSave)
        }
    }

    private fun loadBitmap(call: (bitmap: Bitmap, path: String) -> Unit) {
        binding.isLoading?.value?.let {
            if (!it) {
                binding.isLoading?.value = true
                val url = wallLiveData?.value?.url
                GlideApp.with(WallhavenApp.appComponent.getAppContext())
                    .asBitmap()
                    .load(url)
                    .signature(ObjectKey(System.currentTimeMillis()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onLoadCleared(placeholder: Drawable?) {
                            //nothing
                        }

                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            call(resource, pathToSave)
                            binding.isLoading?.value = false
                        }
                    })
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        GlideApp.get(WallhavenApp.appComponent.getAppContext()).clearMemory()
        lifecycleOwner?.let { wallLiveData?.removeObservers(it) }
    }

    companion object {
        const val TAG = "ViewerOverlay"
    }
}