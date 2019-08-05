package com.netsoftware.wallpool.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.netsoftware.wallpool.data.dataSources.local.TagDao
import com.netsoftware.wallpool.data.dataSources.local.UserDao
import com.netsoftware.wallpool.data.dataSources.local.WallpaperDao
import com.netsoftware.wallpool.data.models.Tag
import com.netsoftware.wallpool.data.models.User
import com.netsoftware.wallpool.data.models.Wallpaper

@Database(entities = [User::class, Wallpaper::class, Tag::class], version = 2)
@TypeConverters(Converters::class)
abstract class WallpoolDB : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun wallpaperDao(): WallpaperDao
    abstract fun tagDao(): TagDao

}