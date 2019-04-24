package com.netsoftware.wallhaven.utility.di

import android.content.Context
import com.google.gson.Gson
import com.netsoftware.wallhaven.WallhavenApp
import com.netsoftware.wallhaven.data.WallhavenDB
import com.netsoftware.wallhaven.data.dataSources.local.SharedPrefs
import com.netsoftware.wallhaven.data.dataSources.remote.WallhavenApi
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
interface AppComponent : AndroidInjector<WallhavenApp> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: WallhavenApp): Builder

        fun build(): AppComponent
    }

    @Named("nested") fun getGson(): Gson
    fun getDB(): WallhavenDB
    fun getSharedPrefs(): SharedPrefs
    fun getAppContext(): Context
    fun getWallhavenApi(): WallhavenApi
}