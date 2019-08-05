package com.netsoftware.wallpool.utility.di

import android.content.Context
import com.google.gson.Gson
import com.netsoftware.wallpool.WallpoolApp
import com.netsoftware.wallpool.data.WallpoolDB
import com.netsoftware.wallpool.data.dataSources.local.SharedPrefs
import com.netsoftware.wallpool.data.dataSources.remote.WallhavenApi
import com.netsoftware.wallpool.data.repositories.UserRepository
import com.netsoftware.wallpool.data.repositories.WallpaperRepository
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Named
import javax.inject.Singleton

@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class,
        AppModule::class,
        ViewModelBuilder::class,
        DataModule::class
    ])
@Singleton
interface AppComponent : AndroidInjector<WallpoolApp> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: WallpoolApp): Builder

        fun build(): AppComponent
    }

    @Named("nested") fun getGson(): Gson
    fun getDB(): WallpoolDB
    fun getSharedPrefs(): SharedPrefs
    fun getAppContext(): Context
    fun getWallhavenApi(): WallhavenApi
    fun getUserRepository(): UserRepository
    fun getWallpaperRepository(): WallpaperRepository
}