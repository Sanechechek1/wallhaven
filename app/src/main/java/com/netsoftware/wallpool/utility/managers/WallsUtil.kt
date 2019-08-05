package com.netsoftware.wallpool.utility.managers

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import com.netsoftware.wallpool.R
import com.netsoftware.wallpool.WallpoolApp
import java.io.IOException


class WallsUtil(private val context: Context) {
    private val wallManager = WallpaperManager.getInstance(context)

    private fun setHomeWall(bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(WallpoolApp.TAG, "setWall: before bitmapSet - ${System.currentTimeMillis()}")
            wallManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
        } else
            wallManager.setBitmap(bitmap)
//        Toast.makeText(context, "Home Screen Wallpaper Changed", Toast.LENGTH_SHORT).show()
    }

    private fun setLockWall(bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            wallManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
        } else
            wallManager.setBitmap(bitmap)
//        Toast.makeText(context, "Lock Screen Wallpaper Changed", Toast.LENGTH_SHORT).show()
    }

    private fun setBothWall(bitmap: Bitmap) {
        setHomeWall(bitmap)
        setLockWall(bitmap)
//        Toast.makeText(context, "Both Wallpapers Changed", Toast.LENGTH_SHORT).show()
    }

    @Throws(IOException::class)
    fun setWall(bitmap: Bitmap, type: SetWallType) {
        when (type) {
            SetWallType.HOME -> setHomeWall(bitmap)
            SetWallType.LOCK -> setLockWall(bitmap)
            SetWallType.BOTH -> setBothWall(bitmap)
        }
    }

    enum class SetWallType(val changedText: Int) {
        HOME(R.string.home_wall_changed),
        LOCK(R.string.lock_wall_changed),
        BOTH(R.string.both_wall_changed)
    }
}