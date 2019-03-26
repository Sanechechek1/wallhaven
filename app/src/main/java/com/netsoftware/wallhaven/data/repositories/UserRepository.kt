package com.netsoftware.wallhaven.data.repositories

import android.os.Handler
import com.netsoftware.wallhaven.utility.managers.NetManager
import javax.inject.Inject

class UserRepository @Inject constructor(netManager: NetManager){
    var counter = 0

    fun refreshData(onDataReadyCallback: OnDataReadyCallback){
        Handler().postDelayed({ onDataReadyCallback.onDataReady("New User #$counter. Congrats!") },3000)
    }

    interface OnDataReadyCallback{
        fun onDataReady(data: String)
    }
}