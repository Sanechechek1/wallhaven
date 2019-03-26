package com.netsoftware.wallhaven.utility.di

import com.netsoftware.wallhaven.WallhavenApp
import com.netsoftware.wallhaven.data.WallhavenDB
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import me.mladenrakonjac.modernandroidapp.utility.di.ViewModelBuilder
import javax.inject.Singleton

@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        ActivityBindingModule::class,
        AppModule::class,
        ViewModelBuilder::class,
        RoomModule::class
    ])
@Singleton
interface AppComponent : AndroidInjector<WallhavenApp>{

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: WallhavenApp): Builder

        fun build(): AppComponent
    }

    fun getDB(): WallhavenDB
}