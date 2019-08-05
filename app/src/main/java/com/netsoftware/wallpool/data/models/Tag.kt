package com.netsoftware.wallpool.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey
    @SerializedName("id")
    var id: Long = 0,

    @SerializedName("name")
    var name: String = ""
)