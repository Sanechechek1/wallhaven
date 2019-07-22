package com.netsoftware.wallhaven

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
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


class WallhavenApp : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        checkScreenDimension()
        initIconFonts()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        appComponent = DaggerAppComponent.builder().application(this).build()
        return appComponent
    }

    private fun checkScreenDimension() {
        val prefs = appComponent.getSharedPrefs()
        if (prefs.screenResolution.isEmpty()) {
            prefs.screenResolution = MyDisplayManager.findResolution()
            prefs.screenRatio = MyDisplayManager.findRatio()
        }
        if (prefs.userId == 0L) {
            populateDB()
        }
    }

    private fun populateDB() {
        runBlocking {
            appComponent.getSharedPrefs().userId =
                withContext(Dispatchers.IO) {
                    appComponent.getDB().userDao().simpleInsert(User(apiKey = "6kJO7b9FEEUOHpqRl6PZBBbjzkrfBkSY"))
                }
        }
    }

    private fun initIconFonts() {
        Iconics.init(this)
        Iconics.registerFont(EssentialPack())
        Iconics.registerFont(EssentialPackFilled())
    }

    override fun onLowMemory() {
        super.onLowMemory()
//        GlideApp.get(applicationContext).clearMemory()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
//        GlideApp.get(applicationContext).trimMemory(level)
    }

    companion object {
        lateinit var appComponent: AppComponent
        const val TAG = "WallhavenApp"

        fun hideKeyboard(view: View) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun showKeyboard(view: View) {
            val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, 0)
        }

        fun getContentUti(context: Context, path: String): Uri? {
            val result: Uri?
            val cursor = context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                MediaStore.Images.Media.DATA + "=? ",
                arrayOf(path),
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                result = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(id))
            } else if (path.isNotEmpty()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, path)
                result = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
                )
            } else {
                result = null
            }
            cursor?.close()
            return result
        }
    }
}