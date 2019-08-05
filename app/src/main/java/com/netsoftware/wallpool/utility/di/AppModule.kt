package com.netsoftware.wallpool.utility.di

import android.app.Application
import android.content.Context
import com.netsoftware.wallpool.WallpoolApp
import dagger.Module
import dagger.Provides

@Module
class AppModule {

    @Provides
    fun providesContext(application: WallpoolApp): Context {
        return application.applicationContext
    }

    @Provides
    fun providesApplication(application: WallpoolApp): Application {
        return application
    }
}