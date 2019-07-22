package com.netsoftware.wallhaven.utility.managers

import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import com.netsoftware.wallhaven.R
import com.netsoftware.wallhaven.WallhavenApp
import com.netsoftware.wallhaven.data.dataSources.local.SharedPrefs

object MyDisplayManager {
    val delimiter: String = WallhavenApp.appComponent.getAppContext().getString(R.string.delimiter)
    private val displaySize: Point by lazy {
        Point().also {
            (WallhavenApp.appComponent.getAppContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay
                .apply { getSize(it) }
        }
    }

    private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

    fun findRatio(): String {
        val gcd = gcd(displaySize.x, displaySize.y)
        return "${displaySize.x / gcd}$delimiter${displaySize.y / gcd}"
    }

    fun findRatio(resolution: String): String {
        val resSlices = resolution.split(delimiter).mapNotNull { it.toIntOrNull() }
        return if (resSlices.size == 2) {
            val gcd = gcd(resSlices[0], resSlices[1])
            "${resSlices[0] / gcd}$delimiter${resSlices[1] / gcd}"
        } else ""
    }

    fun findResolution(): String {
        return "${displaySize.x}$delimiter${displaySize.y}"
    }

    fun getResolution(width: Int, height: Int): String {
        return "$width$delimiter$height"
    }

    fun getCurrentDisplaySize(context: Context): Point {
        return Point().also {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager)
                .defaultDisplay
                .apply { getSize(it) }
        }
    }

    fun getRatioX() = SharedPrefs.getSharedPrefs().screenRatio.split(delimiter).first()

    fun getRatioY() = SharedPrefs.getSharedPrefs().screenRatio.split(delimiter).last()
}