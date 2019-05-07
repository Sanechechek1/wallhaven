package com.netsoftware.wallhaven.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.netsoftware.wallhaven.data.dataSources.local.UserDao
import com.netsoftware.wallhaven.data.dataSources.local.WallpaperDao
import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.data.models.Wallpaper

@Database(entities = [User::class, Wallpaper::class], version = 1)
@TypeConverters(Converters::class)
abstract class WallhavenDB : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun wallpaperDao(): WallpaperDao

}