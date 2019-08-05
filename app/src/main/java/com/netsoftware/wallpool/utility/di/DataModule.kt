package com.netsoftware.wallpool.utility.di

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.netsoftware.wallpool.data.MyDeserializer
import com.netsoftware.wallpool.data.WallpoolDB
import com.netsoftware.wallpool.data.dataSources.local.TagDao
import com.netsoftware.wallpool.data.dataSources.local.UserDao
import com.netsoftware.wallpool.data.dataSources.local.WallpaperDao
import com.netsoftware.wallpool.data.dataSources.remote.WallhavenApi
import com.netsoftware.wallpool.data.models.User
import com.netsoftware.wallpool.data.models.Wallpaper
import com.netsoftware.wallpool.utility.managers.MyDateFormat
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton




@Module
class DataModule{
    @Singleton
    @Provides
    fun providesRoomDatabase(application: Application): WallpoolDB {
        return Room
            .databaseBuilder(application, WallpoolDB::class.java, "wallpool-db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun providesUserDao(database: WallpoolDB): UserDao {
        return database.userDao()
    }

    @Singleton
    @Provides
    fun providesWallpaperDao(database: WallpoolDB): WallpaperDao {
        return database.wallpaperDao()
    }

    @Singleton
    @Provides
    fun providesTagDao(database: WallpoolDB): TagDao {
        return database.tagDao()
    }

    @Provides
    @Singleton
    fun provideGsonUnpacker(): Gson{
        return GsonBuilder()
            .registerTypeAdapter(User::class.java, MyDeserializer<User>())
            .registerTypeAdapter(Wallpaper::class.java, MyDeserializer<Wallpaper>())
            .registerTypeAdapter(object : TypeToken<MutableList<Wallpaper>>(){}.type, MyDeserializer<MutableList<Wallpaper>>())
            .setDateFormat(MyDateFormat.serverDateFormat.toPattern())
            .create()
    }

    @Provides
    @Singleton
    @Named("nested")
    fun provideNestedGson(): Gson{
        return GsonBuilder()
            .setDateFormat(MyDateFormat.serverDateFormat.toPattern())
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): WallhavenApi{
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://wallhaven.cc/api/v1/")
            .build().create(WallhavenApi::class.java)
    }
}