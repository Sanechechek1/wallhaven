package com.netsoftware.wallhaven.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey
    @SerializedName("id")
    var id: Long = 0,

    @SerializedName("name")
    var name: String = ""
)