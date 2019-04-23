package com.netsoftware.wallhaven.utility.managers

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import com.netsoftware.wallhaven.WallhavenApp

object MyDisplayManager{
    private const val resolutionDelimiter = "x"
    private val displaySize: Point by lazy {
        Point().also {
            (WallhavenApp.appComponent.getAppContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay
                .apply{getSize(it)}
        }
    }

    private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

    fun findRatio(): String{
        val gcd = gcd(displaySize.x, displaySize.y)
        return "${displaySize.x/gcd}$resolutionDelimiter${displaySize.y/gcd}"
    }

    fun findRatio(resolution: String): String{
        val resSlices = resolution.split(resolutionDelimiter)
        return if(resSlices.size>1){
            val gcd = gcd(displaySize.x, displaySize.y)
            "${displaySize.x/gcd}$resolutionDelimiter${displaySize.y/gcd}"
        } else ""
    }

    fun findResolution(): String{
        return "${displaySize.x}$resolutionDelimiter${displaySize.y}"
    }
}