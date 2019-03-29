package com.netsoftware.wallhaven

import android.util.Log
import com.amitshekhar.DebugDB
import com.netsoftware.wallhaven.utility.di.AppComponent
import com.netsoftware.wallhaven.utility.di.DaggerAppComponent
import com.netsoftware.wallhaven.utility.managers.MyDisplayManager
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication


class WallhavenApp : DaggerApplication(){

    override fun onCreate() {
        super.onCreate()
        checkScreenDimension()
        if(BuildConfig.DEBUG) Log.w("DEBUG-DB", DebugDB.getAddressLog())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerAppComponent.builder().application(this).build()
        return appComponent
    }

    private fun checkScreenDimension(){
        val prefs = appComponent.getSharedPrefs()
        if(prefs.screenResolution.isEmpty()){
            prefs.screenResolution = MyDisplayManager.findResolution()
            prefs.screenRatio = MyDisplayManager.findRatio()
        }
    }

    companion object {
        lateinit var appComponent: AppComponent
        const val TAG = "WallhavenApp"
    }
}