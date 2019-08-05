package com.netsoftware.wallpool.data.dataSources.local

import android.app.Application
import com.chibatching.kotpref.KotprefModel
import com.netsoftware.wallpool.WallpoolApp
import com.netsoftware.wallpool.data.models.User.Companion.THUMB_ORIGINAL
import javax.inject.Inject

class SharedPrefs @Inject constructor(context: Application): KotprefModel(context){
    override val kotprefName: String = "wh_prefs"
    var userId by longPref()
    var userApiKey by stringPref()
    var screenResolution by stringPref()
    var screenRatio by stringPref()
    var thumbSize by stringPref(THUMB_ORIGINAL)
    var suitableRatioOn by booleanPref(false)

    companion object{
        fun getSharedPrefs() = WallpoolApp.appComponent.getSharedPrefs()
    }
}
