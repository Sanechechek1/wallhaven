package com.netsoftware.wallhaven

import com.netsoftware.wallhaven.utility.di.AppComponent
import com.netsoftware.wallhaven.utility.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication


class WallhavenApp : DaggerApplication(){

    companion object {
        lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerAppComponent.builder().application(this).build()
        return appComponent
    }
}