package com.netsoftware.wallhaven.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.netsoftware.wallhaven.data.enums.Categories
import com.netsoftware.wallhaven.data.enums.Purity
import com.netsoftware.wallhaven.data.enums.ThumbSize
import com.netsoftware.wallhaven.data.enums.TopListRange

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var apiKey: String = "",

    @SerializedName("thumb_size")
    var thumbSize: String = ThumbSize.SMALL.value,

    @SerializedName("per_page")
    var wallPerPage: Int = 24,

    @SerializedName("toplist_range")
    var topListRange: String = TopListRange.MONTH.value,

    @SerializedName("purity")
    var purity: MutableList<String> = mutableListOf(Purity.SFW.value, Purity.SKETCHY.value),

    @SerializedName("categories")
    var categories: MutableList<String> = mutableListOf(Categories.GENERAL.value, Categories.ANIME.value, Categories.PEOPLE.value),

    @SerializedName("resolutions")
    var resolutions: MutableList<String> = mutableListOf(),

    @SerializedName("aspect_ratios")
    var aspectRatio: MutableList<String> = mutableListOf(),

    @SerializedName("tag_blacklist")
    var tagBlackMutableList: MutableList<String> = mutableListOf(),

    @SerializedName("user_blacklist")
    var userBlockMutableList: MutableList<String> = mutableListOf()
)
