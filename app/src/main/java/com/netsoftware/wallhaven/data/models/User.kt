package com.netsoftware.wallhaven.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.netsoftware.wallhaven.data.enums.Categories
import com.netsoftware.wallhaven.data.enums.Purity
import com.netsoftware.wallhaven.data.enums.ThumbSize
import com.netsoftware.wallhaven.data.enums.TopListRange

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var apiKey: String = "",
    var thumbSize: String = ThumbSize.SMALL.value,
    var wallPerPage: Int = 24,
    var topListRange: String = TopListRange.MONTH.value,
    var purity: MutableList<String> = mutableListOf(Purity.SFW.value, Purity.SKETCHY.value),
    var categories: MutableList<String> = mutableListOf(Categories.GENERAL.value, Categories.ANIME.value, Categories.PEOPLE.value),
    var resolutions: MutableList<String> = mutableListOf(),
    var aspectRatio: MutableList<String> = mutableListOf(),
    var tagBlackMutableList: MutableList<String> = mutableListOf(),
    var userBlockMutableList: MutableList<String> = mutableListOf()
)
