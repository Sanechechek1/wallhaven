package com.netsoftware.wallhaven.data.dataSources.local

import android.content.Context
import com.chibatching.kotpref.KotprefModel
import javax.inject.Inject

class SharedPrefs @Inject constructor(context: Context): KotprefModel(context){
    override val kotprefName: String = "wh_prefs"
    var userId by longPref()
    var userApiKey by stringPref()
    var screenResolution by stringPref()
    var screenRatio by stringPref()
}
