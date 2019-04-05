package com.netsoftware.wallhaven.utility.di

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.netsoftware.wallhaven.data.MyDeserializer
import com.netsoftware.wallhaven.data.WallhavenDB
import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.data.repositories.dataSources.local.UserDao
import com.netsoftware.wallhaven.data.repositories.dataSources.remote.WallhavenApi
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class DataModule{
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

    @Provides
    @Singleton
    fun provideGson(): Gson{
        return GsonBuilder()
            .registerTypeAdapter(User::class.java, MyDeserializer<User>())
            .create()
    }

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): WallhavenApi{
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("http://stest39.wallhaven.cc/api/v1/")
            .build().create(WallhavenApi::class.java)
    }
}