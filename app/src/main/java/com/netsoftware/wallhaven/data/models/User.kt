package com.netsoftware.wallhaven.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

const val THUMB_SMALL = "small"
const val THUMB_ORIGINAL = "original"
const val THUMB_LARGE = "large"
const val TOPLIST_DAY = "1D"
const val TOPLIST_WEEK = "1W"
const val TOPLIST_MONTH = "1M"
const val TOPLIST_YEAR = "1Y"
const val CATEGORY_GENERAL = "general"
const val CATEGORY_ANIME = "anime"
const val CATEGORY_PEOPLE = "people"
const val PURITY_SFW = "sfw"
const val PURITY_SKETCHY = "sketchy"
const val PURITY_NSFW = "nsfw"

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
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
