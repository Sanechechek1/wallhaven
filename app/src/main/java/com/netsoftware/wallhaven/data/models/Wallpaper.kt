package com.netsoftware.wallhaven.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(tableName = "user")
data class Wallpaper(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var apiKey: String = "",

    @SerializedName("thumb_size")
    var thumbSize: String = THUMB_SMALL,

    @SerializedName("per_page")
    var wallPerPage: Int = 24,

    @SerializedName("toplist_range")
    var topListRange: String = TOPLIST_MONTH,

    @SerializedName("purity")
    var purity: MutableList<String> = mutableListOf(PURITY_SFW, PURITY_SKETCHY),

    @SerializedName("categories")
    var categories: MutableList<String> = mutableListOf(CATEGORY_GENERAL, CATEGORY_ANIME, CATEGORY_PEOPLE),

    @SerializedName("resolutions")
    var resolutions: MutableList<String> = mutableListOf(),

    @SerializedName("aspect_ratios")
    var aspectRatio: MutableList<String> = mutableListOf(),

    @SerializedName("tag_blacklist")
    var tagBlackMutableList: MutableList<String> = mutableListOf(),

    @SerializedName("user_blacklist")
    var userBlockMutableList: MutableList<String> = mutableListOf()
)