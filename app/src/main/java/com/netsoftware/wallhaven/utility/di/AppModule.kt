package com.netsoftware.wallhaven.utility.di

import android.app.Application
import android.content.Context
import com.netsoftware.wallhaven.WallhavenApp
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun providesContext(application: WallhavenApp): Context {
        return application.applicationContext
    }

    @Provides
    fun providesApplication(application: WallhavenApp): Application {
        return application
    }
}