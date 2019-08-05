package com.netsoftware.wallpool.data

import androidx.room.TypeConverter
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.netsoftware.wallpool.WallpoolApp
import com.netsoftware.wallpool.data.models.Tag
import java.lang.reflect.Type
import java.util.*


class Converters {
    @TypeConverter
    fun restoreStringList(listOfString: String): List<String> {
        return Gson().fromJson(listOfString, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun saveStringList(listOfString: List<String>): String {
        return Gson().toJson(listOfString)
    }

    @TypeConverter
    fun restoreMap(value: String): Map<String, String>? {
        val mapType = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson<Map<String, String>>(value, mapType)
    }

    @TypeConverter
    fun saveMap(map: Map<String, String>): String {
        val gson = Gson()
        return gson.toJson(map)
    }

    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        return if (dateLong == null) null else Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun restoreTagList(listOfTag: String): List<Tag> {
        return Gson().fromJson(listOfTag, object : TypeToken<List<Tag>>() {}.type)
    }

    @TypeConverter
    fun saveTagList(listOfTag: List<Tag>): String {
        return Gson().toJson(listOfTag)
    }
}

class MyDeserializer<T> : JsonDeserializer<T> {
    override fun deserialize(je: JsonElement, type: Type, jdc: JsonDeserializationContext): T {
        val unpackedContent =
            when (val content = if (je.isJsonObject) je.asJsonObject["data"] else je) {
                is JsonObject -> checkUnpackedUploader(content)
                is JsonArray -> content.asJsonArray.onEach { checkUnpackedUploader(it.asJsonObject) }
                else -> content
            }
        return WallpoolApp.appComponent.getGson().fromJson(unpackedContent, type)
    }

    private fun checkUnpackedUploader(data: JsonObject): JsonObject {
        if (data.has("uploader")) {
            data.addProperty(
                "uploader",
                data["uploader"].asJsonObject["username"].asString
            )
        }
        return data
    }
}