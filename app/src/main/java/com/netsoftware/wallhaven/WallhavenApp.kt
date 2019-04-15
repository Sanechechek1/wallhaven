package com.netsoftware.wallhaven

import android.util.Log
import com.amitshekhar.DebugDB
import com.mikepenz.essentialpack_typeface_library.EssentialPack
import com.mikepenz.essentialpackfilled_typeface_library.EssentialPackFilled
import com.mikepenz.iconics.Iconics
import com.netsoftware.wallhaven.data.models.User
import com.netsoftware.wallhaven.utility.di.AppComponent
import com.netsoftware.wallhaven.utility.di.DaggerAppComponent
import com.netsoftware.wallhaven.utility.managers.MyDisplayManager
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class WallhavenApp : DaggerApplication(){

    override fun onCreate() {
        super.onCreate()
        checkScreenDimension()
        initIconFonts()
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
        if(prefs.userId==0L){
           populateDB()
        }
    }

    private fun populateDB(){
        runBlocking {
            appComponent.getSharedPrefs().userId =
            withContext(Dispatchers.IO) {
                appComponent.getDB().userDao().simpleInsert(User(apiKey = "6kJO7b9FEEUOHpqRl6PZBBbjzkrfBkSY"))
            }
        }
    }

    private fun initIconFonts(){
        Iconics.init(this)
        Iconics.registerFont(EssentialPack())
        Iconics.registerFont(EssentialPackFilled())
    }

    companion object {
        lateinit var appComponent: AppComponent
        const val TAG = "WallhavenApp"
    }
}