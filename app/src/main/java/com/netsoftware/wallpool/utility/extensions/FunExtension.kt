package com.netsoftware.wallpool.utility.extensions

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import androidx.navigation.NavArgument
import com.google.android.material.snackbar.Snackbar
import com.netsoftware.wallpool.data.models.Tag
import com.netsoftware.wallpool.data.models.User.Companion.THUMB_ORIGINAL
import com.netsoftware.wallpool.data.models.Wallpaper
import com.netsoftware.wallpool.utility.managers.MyDisplayManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.serialization.json.JSON
import java.text.DecimalFormat

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.spToPx(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics)
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable = apply { compositeDisposable.add(this) }

fun `in`.nerd_is.wallhaven4kotlin.model.Wallpaper.convert(): Wallpaper {
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
        uploaderName = this.uploader.name,
        colors = this.colors.map { String.format("#%02x%02x%02x", it.r, it.g, it.b) }.toMutableList(),
        thumbs = mutableMapOf(THUMB_ORIGINAL to this.thumbnailUrl),
        tags = this.tags.map { Tag(it.id, it.name) }.toMutableList()
    )
}

fun String.sizeToReadable(): String {
    val format = DecimalFormat("#.##")
    val megabyte = 1024 * 1024
    val kilobyte = 1024
    val size = this.toLong()
    if (size > megabyte) {
        return format.format(size / megabyte) + " MiB"
    }
    if (size > kilobyte) {
        return format.format(size / kilobyte) + " KiB"
    }
    return format.format(size) + " B"
}

fun Map<String, NavArgument>.toBundle(): Bundle {
    val result = Bundle()
    for (entry in this.entries)
        result.putString(entry.key, entry.value.toString())
    return result
}

fun Snackbar.changeColors(): Snackbar{
    this.setTextColor(this.context.getColor(com.netsoftware.wallpool.R.color.primary_light_text))
    this.setBackgroundTint(this.context.getColor(com.netsoftware.wallpool.R.color.gray))
    return this
}

fun String.isJsonTagValid(): Boolean {
    try {
        JSON.parse(Tag.serializer(),this)
    } catch (ex: Exception) {
            return false
    }
    return true
}
