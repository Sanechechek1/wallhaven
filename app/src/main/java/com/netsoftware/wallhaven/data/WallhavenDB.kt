package com.netsoftware.wallhaven.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.data.dataSources.local.UserDao

@Database(entities = [User::class], version = 1)
@TypeConverters(Converters::class)
abstract class WallhavenDB : RoomDatabase() {

    abstract fun userDao(): UserDao

}