package com.netsoftware.wallhaven.data.dataSources.local

import android.app.Application
import com.chibatching.kotpref.KotprefModel
import com.netsoftware.wallhaven.WallhavenApp
import com.netsoftware.wallhaven.data.models.THUMB_ORIGINAL
import javax.inject.Inject

class SharedPrefs @Inject constructor(context: Application): KotprefModel(context){
    override val kotprefName: String = "wh_prefs"
    var userId by longPref()
    var userApiKey by stringPref()
    var screenResolution by stringPref()
    var screenRatio by stringPref()
    var thumbSize by stringPref(THUMB_ORIGINAL)

    companion object{
        fun getSharedPrefs() = WallhavenApp.appComponent.getSharedPrefs()
    }
}
