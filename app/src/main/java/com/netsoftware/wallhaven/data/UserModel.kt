package com.netsoftware.wallhaven.data

import android.os.Handler

class UserModel {
    var counter = 0

    fun refreshData(onDataReadyCallback: OnDataReadyCallback){
        Handler().postDelayed({ onDataReadyCallback.onDataReady("New User #$counter. Congrats!") },3000)
    }

    interface OnDataReadyCallback{
        fun onDataReady(data: String)
    }
}