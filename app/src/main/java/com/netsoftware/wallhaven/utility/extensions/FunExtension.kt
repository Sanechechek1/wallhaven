package com.netsoftware.wallhaven.utility.extensions

import android.content.res.Resources
import com.netsoftware.wallhaven.data.models.THUMB_ORIGINAL
import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.data.models.Wallpaper
import com.netsoftware.wallhaven.utility.managers.MyDisplayManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable
        = apply { compositeDisposable.add(this)}

fun `in`.nerd_is.wallhaven4kotlin.model.Wallpaper.convert(): Wallpaper{
    return Wallpaper(
        id = this.id.toString(),
        url = this.fullUrl,
        category = this.category.name,
        favorites = this.favCount.toInt(),
        fileSize = this.size,
        creationDate = this.uploadTime,
        purity = this.purity.name,
        views = this.viewCount.toInt(),
        resolution = MyDisplayManager.getResolution(this.resolution.width, this.resolution.height),
        uploader = User(
            avatars = mutableMapOf("32px" to this.uploader.avatarThumbnail),
            name = this.uploader.name
        ),
        colors = this.colors.map { String.format("#%02x%02x%02x", it.r, it.g, it.b)}.toMutableList(),
        thumbs = mutableMapOf(THUMB_ORIGINAL to this.thumbnailUrl)
    )
}

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
