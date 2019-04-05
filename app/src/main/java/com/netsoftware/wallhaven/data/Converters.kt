package com.netsoftware.wallhaven.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class Converters{
    @TypeConverter
    fun restoreList(listOfString: String): List<String> {
        return Gson().fromJson(listOfString, object : TypeToken<List<String>>(){}.type)
    }

    @TypeConverter
    fun saveList(listOfString: List<String>): String {
        return Gson().toJson(listOfString)
    }
}

class MyDeserializer<T> : JsonDeserializer<T> {
    override fun deserialize(je: JsonElement, type: Type, jdc: JsonDeserializationContext): T {
        val content = je.asJsonObject.get("data")
        return Gson().fromJson(content, type)
    }
}