package com.netsoftware.wallpool.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.netsoftware.wallpool.data.models.User.Companion.CATEGORY_GENERAL
import com.netsoftware.wallpool.data.models.User.Companion.PURITY_SFW
import com.netsoftware.wallpool.utility.managers.MyDisplayManager
import java.util.*

@Entity(tableName = "wallpaper")
data class Wallpaper(
    @PrimaryKey
    @SerializedName("id")
    var id: String = UUID.randomUUID().toString(),

    @SerializedName("path")
    var url: String = "",

    @SerializedName("url")
    var postUrl: String = "",

    @SerializedName("uploader")
    var uploaderName: String = "",

    @SerializedName("views")
    var views: Int = 0,

    @SerializedName("favorites")
    var favorites: Int = 0,

    @SerializedName("purity")
    var purity: String = PURITY_SFW,

    @SerializedName("category")
    var category: String = CATEGORY_GENERAL,

    @SerializedName("resolution")
    var resolution: String = "",

    @Expose
    var ratio: String = MyDisplayManager.findRatio(resolution),

    @Expose
    var isLiked: Boolean = false,

    @SerializedName("file_size")
    var fileSize: String = "",

    @SerializedName("file_type")
    var fileType: String = "",

    @SerializedName("created_at")
    var creationDate: Date = Date(),

    @SerializedName("colors")
    var colors: MutableList<String> = mutableListOf(),

    @SerializedName("thumbs")
    var thumbs: MutableMap<String, String> = mutableMapOf(),

    @SerializedName("tags")
    var tags: MutableList<Tag> = mutableListOf()
)