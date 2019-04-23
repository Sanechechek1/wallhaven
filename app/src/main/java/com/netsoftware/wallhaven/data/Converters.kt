package com.netsoftware.wallhaven.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.netsoftware.wallhaven.WallhavenApp
import java.lang.reflect.Type
import java.util.*


class Converters{
    @TypeConverter
    fun restoreList(listOfString: String): List<String> {
        return Gson().fromJson(listOfString, object : TypeToken<List<String>>(){}.type)
    }

    @TypeConverter
    fun saveList(listOfString: List<String>): String {
        return Gson().toJson(listOfString)
    }

    @TypeConverter
    fun restoreMap(value: String): Map<String, String>? {
        val mapType = object : TypeToken<Map<String, String>>(){}.type
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
}

class MyDeserializer<T> : JsonDeserializer<T> {
    override fun deserialize(je: JsonElement, type: Type, jdc: JsonDeserializationContext): T {
        val content = je.asJsonObject.getAsJsonObject("data")
        if(content.has("created_at")){
            content.addProperty("created_at", content["created_at"].asJsonObject["date"].asString)
        }
        return WallhavenApp.appComponent.getGson().fromJson(content, type)
    }
}