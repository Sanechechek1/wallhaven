package com.netsoftware.wallhaven.utility.di

import android.app.Application
import androidx.room.Room
import com.netsoftware.wallhaven.data.WallhavenDB
import com.netsoftware.wallhaven.data.daos.UserDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class RoomModule{
    @Singleton
    @Provides
    fun providesRoomDatabase(application: Application): WallhavenDB {
        return Room
            .databaseBuilder(application, WallhavenDB::class.java, "wallhaven-db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesProductDao(demoDatabase: WallhavenDB): UserDao {
        return demoDatabase.userDao()
    }
}